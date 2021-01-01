/**
 * Copyright (C) 2019-2021 Philip Helger
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
package com.helger.registry434.supplementary.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.csv.CSVReader;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.string.StringHelper;
import com.helger.registry434.domain.EDataType;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;

public final class MainReadBTs
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainReadBTs.class);

  public static void main (final String [] args) throws IOException
  {
    final File f = new File ("src/test/resources/ensrc/bts.csv");
    try (final CSVReader r = new CSVReader (FileHelper.getBufferedReader (f, StandardCharsets.UTF_8)))
    {
      r.setSeparatorChar (';').setSkipLines (1);

      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.appendElement ("root");
      final NonBlockingStack <IMicroElement> aStack = new NonBlockingStack <> ();
      aStack.push (eRoot);

      ICommonsList <String> aLine;
      String sPrevID = null;
      int nPrevLevel = 0;
      while ((aLine = r.readNext ()) != null)
      {
        String sID = aLine.get (0);
        if (StringHelper.hasNoText (sID))
        {
          // For BTs with many items
          sID = sPrevID;

          // Ignore for now
          continue;
        }

        final String sLevel = aLine.get (1);
        final int nLevel = sLevel.length ();
        if (nLevel <= 0)
          throw new IllegalStateException ("Invalid level '" + sLevel + "'");

        final String sCardinality = aLine.get (2);
        final ECardinalityType eCard = ECardinalityType.getFromIDOrNull (sCardinality);
        if (eCard == null)
          throw new IllegalStateException ("Unsupported cardinality '" + sCardinality + "'");

        final String sBT = aLine.get (3);
        final String sSemDT = aLine.get (4);

        if (nLevel < nPrevLevel)
          for (int i = 0; i < nPrevLevel - nLevel; ++i)
            aStack.pop ();

        final boolean bIsBG = sID.startsWith ("BG-");
        if (bIsBG)
        {

          final IMicroElement eGroup = aStack.peek ().appendElement ("business-group");
          eGroup.setAttribute ("id", sID);
          eGroup.setAttribute ("name", sBT);
          eGroup.setAttribute ("card", sCardinality);
          aStack.push (eGroup);
        }
        else
        {
          final EDataType eDT = ArrayHelper.findFirst (EDataType.values (), x -> x.getDisplayName ().equals (sSemDT));
          if (eDT == null)
            throw new IllegalStateException ("Failed to resolve data type '" + sSemDT + "' for ID '" + sID + "'");

          final IMicroElement eTerm = aStack.peek ().appendElement ("business-term");
          eTerm.setAttribute ("id", sID);
          eTerm.setAttribute ("dt", eDT.getID ());
          eTerm.setAttribute ("name", sBT);
          eTerm.setAttribute ("card", sCardinality);
        }

        sPrevID = sID;
        nPrevLevel = nLevel;
      }

      MicroWriter.writeToFile (aDoc, new File ("src/main/resources/codelist/bts.xml"));
    }
    LOGGER.info ("Done");
  }
}
