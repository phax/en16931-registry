/**
 * Copyright (C) 2019 Philip Helger
 * http://www.helger.com
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.registry434.ui;

import java.util.Locale;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.string.StringHelper;
import com.helger.html.request.IHCRequestField;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Object status select.
 *
 * @author Philip Helger
 */
public class HCBTSelect extends HCExtSelect
{
  private static final IMicroDocument aDoc = MicroReader.readMicroXML (new ClassPathResource ("codelist/bts.xml"));

  public HCBTSelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);

    final NonBlockingStack <String> aStack = new NonBlockingStack <> ();
    _read (aDoc.getDocumentElement (),
           aStack,
           (x, s) -> addOption (x.getAttributeValue ("id"),
                                (s.isEmpty () ? "" : StringHelper.getImploded (" / ", s) + " / ") +
                                                            x.getAttributeValue ("id") +
                                                            " " +
                                                            x.getAttributeValue ("name") +
                                                            " [" +
                                                            x.getAttributeValue ("card") +
                                                            "]"));

    addOptionPleaseSelect (aDisplayLocale);
  }

  private void _read (final IMicroElement aStart,
                      final NonBlockingStack <String> aStack,
                      final BiConsumer <IMicroElement, NonBlockingStack <String>> aObject)
  {
    for (final IMicroElement e : aStart.getAllChildElements ())
      if (e.getTagName ().equals ("business-group"))
      {
        aObject.accept (e, aStack);
        aStack.push (e.getAttributeValue ("id") + " " + e.getAttributeValue ("name"));
        _read (e, aStack, aObject);
        aStack.pop ();
      }
      else
        aObject.accept (e, aStack);
  }
}
