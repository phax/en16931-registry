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
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.string.StringHelper;
import com.helger.html.request.IHCRequestField;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.registry434.app.BTManager;

/**
 * Business term select.
 *
 * @author Philip Helger
 */
public class HCBTSelect extends HCExtSelect
{
  private static final ICommonsOrderedMap <String, String> MAP = new CommonsLinkedHashMap <> ();

  static
  {
    BTManager.foreach ( (aStack,
                         aItem) -> MAP.put (aItem.getID (),
                                            (aStack.isEmpty () ? ""
                                                               : StringHelper.getImplodedMapped (" / ",
                                                                                                 aStack,
                                                                                                 x -> x.getID () +
                                                                                                      " " +
                                                                                                      x.getName ()) +
                                                                 " / ") +
                                                            aItem.getID () +
                                                            " " +
                                                            aItem.getName () +
                                                            " [" +
                                                            aItem.getCard () +
                                                            "]"));
  }

  public HCBTSelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);

    for (final Map.Entry <String, String> aEntry : MAP.entrySet ())
      addOption (aEntry.getKey (), aEntry.getValue ());

    addOptionPleaseSelect (aDisplayLocale);
  }
}
