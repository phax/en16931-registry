package com.helger.registry434.page;

import java.util.Locale;

import com.helger.commons.compare.ESortOrder;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.ICEHeader;

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
                                        new DTCol ("Type")).setID (getID ());
    for (final ICEHeader aItem : aCEHeaderMgr.getAll ())
    {
      final HCRow aRow = aTable.addBodyRow ();

      aRow.addCell (aItem.getID ());
      aRow.addCell (aItem.getName ());
      aRow.addCell (aItem.getType ().getDisplayName ());
    }
    aNodeList.addChild (aTable);
    aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
  }
}
