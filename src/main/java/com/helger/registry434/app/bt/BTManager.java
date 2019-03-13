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
package com.helger.registry434.app.bt;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Business term manager
 *
 * @author Philip Helger
 */
public final class BTManager
{
  private static final IMicroDocument BT_DOC = MicroReader.readMicroXML (new ClassPathResource ("codelist/bts.xml"));
  private static final ICommonsOrderedMap <String, AbstractBT> MAP = new CommonsLinkedHashMap <> ();

  private static void _read (@Nonnull final IMicroElement aStart,
                             @Nonnull final NonBlockingStack <BusinessGroup> aStack,
                             @Nonnull final BiConsumer <NonBlockingStack <BusinessGroup>, AbstractBT> aConsumer)
  {
    for (final IMicroElement e : aStart.getAllChildElements ())
      if (e.getTagName ().equals ("business-group"))
      {
        final BusinessGroup aBG = new BusinessGroup (aStack.getLast (),
                                                     e.getAttributeValue ("id"),
                                                     e.getAttributeValue ("name"),
                                                     e.getAttributeValue ("card"));
        aConsumer.accept (aStack, aBG);
        aStack.push (aBG);
        _read (e, aStack, aConsumer);
        aStack.pop ();
      }
      else
        if (e.getTagName ().equals ("business-term"))
        {
          // Business term
          final BusinessTerm aBT = new BusinessTerm (aStack.getLast (),
                                                     e.getAttributeValue ("id"),
                                                     e.getAttributeValue ("name"),
                                                     e.getAttributeValue ("card"),
                                                     e.getAttributeValue ("dt"));
          aConsumer.accept (aStack, aBT);
        }
        else
          throw new IllegalStateException ("Unspported tag " + e.getTagName ());
  }

  static
  {
    // Initialize map
    forEach ( (aStack, aItem) -> {
      MAP.put (aItem.getID (), aItem);
    });
  }

  private BTManager ()
  {}

  public static void forEach (@Nonnull final BiConsumer <NonBlockingStack <BusinessGroup>, AbstractBT> aConsumer)
  {
    final NonBlockingStack <BusinessGroup> aStack = new NonBlockingStack <> ();
    _read (BT_DOC.getDocumentElement (), aStack, aConsumer);
  }

  @Nullable
  public static BusinessGroup findBG (@Nullable final String sID)
  {
    final AbstractBT aItem = MAP.get (sID);
    return aItem instanceof BusinessGroup ? (BusinessGroup) aItem : null;
  }

  @Nullable
  public static BusinessTerm findBT (@Nullable final String sID)
  {
    final AbstractBT aItem = MAP.get (sID);
    return aItem instanceof BusinessTerm ? (BusinessTerm) aItem : null;
  }

  @Nonnull
  @ReturnsMutableObject
  public static ICommonsOrderedMap <String, AbstractBT> map ()
  {
    return MAP;
  }
}
