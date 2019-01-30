package com.helger.registry434.page;

import java.util.Locale;

import com.helger.commons.compare.ESortOrder;
import com.helger.commons.locale.country.CountryCache;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AppCommonUI;

public class PagePublicCEHeaderList extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public PagePublicCEHeaderList (final String sID)
  {
    super (sID, "CIUSs and Extensions");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    final HCTable aTable = new HCTable (new DTCol ("").setVisible (false),
                                        new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Type"),
                                        new DTCol ("Country"),
                                        new DTCol ("Sector"),
                                        new DTCol ("Purpose").setOrderable (false),
                                        new DTCol ("Publisher"),
                                        new DTCol ("Governor"),
                                        new DTCol ("Underlying spec"),
                                        new DTCol ("Further information").setOrderable (false),
                                        new DTCol ("Status"),
                                        new DTCol ("Contact")).setID (getID ());
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
        for (final String sExternalURL : aItem.externalURLs ())
          aCtrl.addChild (new HCDiv ().addChild (HCA.createLinkedWebsite (sExternalURL)));
        aRow.addCell (aCtrl);
      }
      aRow.addCell (AppCommonUI.getUIStatus (aItem.getStatus ()));
      aRow.addCell (AppCommonUI.getUIContact (aItem.getContactName (), aItem.getContactEmail ()));
    }
    aNodeList.addChild (aTable);
    aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
  }
}
