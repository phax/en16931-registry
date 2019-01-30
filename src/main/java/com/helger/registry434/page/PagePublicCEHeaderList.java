package com.helger.registry434.page;

import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public class PagePublicCEHeaderList extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public PagePublicCEHeaderList (final String sID)
  {
    super (sID, "Community-driven Registry of CIUS (Core Invoice Usage Specifications) and Extensions");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (new BootstrapInfoBox ().addChild ("Work in progress"));
  }
}
