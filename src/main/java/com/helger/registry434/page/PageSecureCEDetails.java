package com.helger.registry434.page;

import java.util.Locale;

import com.helger.commons.compare.ESortOrder;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEDetails;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AbstractAppWebPageForm;
import com.helger.registry434.ui.HCBTSelect;
import com.helger.registry434.ui.HCChangeTypeRestrictionSelect;

public class PageSecureCEDetails extends AbstractAppWebPageForm <CEDetails>
{

  public PageSecureCEDetails (final String sID)
  {
    super (sID, "Details");
    // TODO
  }

  @Override
  protected CEDetails getSelectedObject (final WebPageExecutionContext aWPEC, final String sID)
  {
    // TODO
    return null;
  }

  @Override
  protected void showSelectedObject (final WebPageExecutionContext aWPEC, final CEDetails aSelectedObject)
  {
    // TODO

  }

  @Override
  protected void showInputForm (final WebPageExecutionContext aWPEC,
                                final CEDetails aSelectedObject,
                                final BootstrapForm aForm,
                                final boolean bIsFormSubmitted,
                                final EWebPageFormAction eFormAction,
                                final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    // TODO
    final HCNodeList aNL = new HCNodeList ();
    for (int i = 0; i < 5; ++i)
    {
      final BootstrapRow aRow = new BootstrapRow ();
      aRow.createColumn (6).addChild (new HCBTSelect (new RequestField ("bt"), aDisplayLocale));
      aRow.createColumn (5).addChild (new HCChangeTypeRestrictionSelect (new RequestField ("ct"), aDisplayLocale));
      aRow.createColumn (1)
          .addChild (new BootstrapButton ().setIcon (EDefaultIcon.DELETE)
                                           .setButtonType (EBootstrapButtonType.OUTLINE_DANGER));
      aNL.addChild (aRow);
    }
    aNL.addChild (new HCDiv ().addChild (new BootstrapButton ().addChild ("Add restriction")
                                                               .setIcon (EDefaultIcon.PLUS)
                                                               .setButtonType (EBootstrapButtonType.OUTLINE_SUCCESS)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Restriction").setCtrl (aNL));
  }

  @Override
  protected void validateAndSaveInputParameters (final WebPageExecutionContext aWPEC,
                                                 final CEDetails aSelectedObject,
                                                 final FormErrorList aFormErrors,
                                                 final EWebPageFormAction eFormAction)
  {
    // TODO

  }

  @Override
  protected void showListOfExistingObjects (final WebPageExecutionContext aWPEC)
  {
    // TODO

    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    final HCTable aTable = new HCTable (new DTCol ("").setVisible (false),
                                        new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Type"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());
    for (final ICEHeader aItem : aCEHeaderMgr.getAll ())
    {
      final ISimpleURL aViewURL = createViewURL (aWPEC, aItem);
      final HCRow aRow = aTable.addBodyRow ();

      aRow.addCell (aItem.getID ());
      aRow.addCell (new HCA (aViewURL).addChild (aItem.getName ()));
      aRow.addCell (aItem.getType ().getDisplayName ());

      final IHCCell <?> aActionCell = aRow.addCell ();
      aActionCell.addChild (new HCA (createCreateURL (aWPEC)).setTitle ("Edit")
                                                             .addChild (EDefaultIcon.EDIT.getAsNode ()));
      aActionCell.addChild (createDeleteLink (aWPEC, aItem, "Delete"));
    }
    aNodeList.addChild (aTable);
    aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
  }
}
