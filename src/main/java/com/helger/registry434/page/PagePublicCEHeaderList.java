/**
 * Copyright (C) 2019-2020 Philip Helger
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
package com.helger.registry434.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.sections.HCH4;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AppCommonUI;

public class PagePublicCEHeaderList extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public static final String ACTION_DETAILS = "showdetails";

  public PagePublicCEHeaderList (@Nonnull @Nonempty final String sID)
  {
    super (sID, "CIUSs and Extensions");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    boolean bShowList = true;
    if (aWPEC.params ().hasStringValue (CPageParam.PARAM_ACTION, ACTION_DETAILS))
    {
      final String sObjectID = aWPEC.params ().getAsStringTrimmed (CPageParam.PARAM_OBJECT);
      final ICEHeader aHeader = aCEHeaderMgr.getCEHeaderOfID (sObjectID);
      if (aHeader != null && aHeader.hasDetails ())
      {
        // Show details
        aNodeList.addChild (new HCH4 ().addChild ("Details of '" + aHeader.getName () + "'"));

        aNodeList.addChild (AppCommonUI.createDetailsTable (aHeader));

        final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addButtonBack (aDisplayLocale);

        bShowList = false;
      }
    }

    if (bShowList)
    {
      final HCTable aTable = new HCTable (new DTCol ("").setVisible (false),
                                          new DTCol ("Artefact name").setInitialSorting (ESortOrder.ASCENDING),
                                          new DTCol ("Type"),
                                          new DTCol ("Country"),
                                          new DTCol ("Sector"),
                                          new DTCol ("Purpose").setOrderable (false),
                                          new DTCol ("Publisher"),
                                          new DTCol ("Governing Entity"),
                                          new DTCol ("Underlying spec"),
                                          new DTCol ("Further information").setOrderable (false),
                                          new DTCol ("Status"),
                                          new DTCol ("Contact"),
                                          new DTCol ("Details")).setID (getID ());
      for (final ICEHeader aItem : aCEHeaderMgr.getAll ())
      {
        final HCRow aRow = aTable.addBodyRow ();

        aRow.addCell (aItem.getID ());
        aRow.addCell (aItem.getName ());
        aRow.addCell (aItem.getType ().getDisplayName ());
        {
          String sCountry;
          if (aItem.hasCountry ())
          {
            final String sCountryID = aItem.getCountry ();
            final Locale aCountry = CountryCache.getInstance ().getCountry (sCountryID);
            sCountry = aCountry == null ? sCountryID : aCountry.getDisplayCountry (aDisplayLocale);
          }
          else
            sCountry = "Country independent";
          aRow.addCell (sCountry);
        }

        if (aItem.hasSector ())
          aRow.addCell (aItem.getSector ());
        else
          aRow.addCell ("Sector independent");

        // TODO limit
        aRow.addCell (HCExtHelper.nl2divList (aItem.getPurpose ()));
        aRow.addCell (aItem.getPublisher ());
        aRow.addCell (aItem.getGovernor ());
        aRow.addCell (aItem.getUnderlyingSpec ());
        {
          final HCNodeList aCtrl = new HCNodeList ();
          // TODO limit
          aCtrl.addChildren (HCExtHelper.nl2divList (aItem.getFurtherInfo ()));

          int nLink = 1;
          for (final String sExternalURL : aItem.externalURLs ())
          {
            aCtrl.addChild (new HCDiv ().addChild (new HCA (new SimpleURL (sExternalURL)).addChild ("External link " +
                                                                                                    nLink)
                                                                                         .setTargetBlank ()));
            nLink++;
          }
          aRow.addCell (aCtrl);
        }
        aRow.addCell (AppCommonUI.getUIStatus (aItem.getStatus ()));
        aRow.addCell (AppCommonUI.getUIContact (aItem.getContactName (), aItem.getContactEmail ()));

        if (aItem.hasDetails ())
        {
          aRow.addCell (new HCA (aWPEC.getSelfHref ()
                                      .add (CPageParam.PARAM_OBJECT, aItem.getID ())
                                      .add (CPageParam.PARAM_ACTION, ACTION_DETAILS)).addChild ("Details"));
        }
        else
          aRow.addCell ();
      }
      aNodeList.addChild (aTable);
      aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
    }
  }
}
