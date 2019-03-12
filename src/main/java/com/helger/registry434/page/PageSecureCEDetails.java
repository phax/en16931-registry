package com.helger.registry434.page;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.ESortOrder;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.jquery.JQuery;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapFormHelper;
import com.helger.photon.bootstrap4.grid.BootstrapCol;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.core.app.context.ILayoutExecutionContext;
import com.helger.photon.core.app.context.LayoutExecutionContext;
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
import com.helger.registry434.domain.IChangeType;
import com.helger.registry434.ui.AbstractAppWebPageForm;
import com.helger.registry434.ui.HCBTSelect;
import com.helger.registry434.ui.HCChangeTypeRestrictionSelect;
import com.helger.servlet.request.RequestParamMap;

public class PageSecureCEDetails extends AbstractAppWebPageForm <CEDetails>
{
  private static final String PREFIX_CHANGETYPE = "changetype";
  private static final String SUFFIX_CHANGETYPE_BT = "bt";
  private static final String SUFFIX_CHANGETYPE_CTR = "ctr";
  private static final String SUFFIX_CHANGETYPE_DESC = "desc";

  private static final String TMP_ID_PREFIX = "tmp";

  private static final AjaxFunctionDeclaration s_aAjaxAddRowChangeType;

  static
  {
    s_aAjaxAddRowChangeType = addAjax ( (aRequestScope, aAjaxResponse) -> {
      final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
      final IHCNode aNode = _createInputForm (aLEC, (IChangeType) null, (String) null, new FormErrorList ());
      aAjaxResponse.html (aNode);
    });
  }

  @Nonnull
  private static IHCNode _createInputForm (@Nonnull final ILayoutExecutionContext aLEC,
                                           @Nullable final IChangeType aExistingObject,
                                           @Nullable final String sExistingID,
                                           @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sEntityID = StringHelper.hasText (sExistingID) ? sExistingID
                                                                : TMP_ID_PREFIX +
                                                                  Integer.toString (GlobalIDFactory.getNewIntID ());

    final BootstrapRow aRow = new BootstrapRow ().setID (sEntityID);

    {
      final BootstrapCol aCol1 = aRow.createColumn (3);
      final String sFieldBT = RequestParamMap.getFieldName (PREFIX_CHANGETYPE, sEntityID, SUFFIX_CHANGETYPE_BT);
      final IErrorList aErrors = aFormErrors.getListOfField (sFieldBT);
      final HCBTSelect aCtrl = new HCBTSelect (new RequestField (sFieldBT,
                                                                 aExistingObject == null ? null
                                                                                         : aExistingObject.getBtID ()),
                                               aDisplayLocale);
      BootstrapFormHelper.markAsFormControl (aCtrl);
      BootstrapFormHelper.applyFormControlValidityState (aCtrl, aErrors);
      aCol1.addChild (aCtrl).addChild (BootstrapFormHelper.createDefaultErrorNode (aErrors, aDisplayLocale));
    }

    {
      final BootstrapCol aCol2 = aRow.createColumn (3);
      final String sFieldCTR = RequestParamMap.getFieldName (PREFIX_CHANGETYPE, sEntityID, SUFFIX_CHANGETYPE_CTR);
      final IErrorList aErrors = aFormErrors.getListOfField (sFieldCTR);
      final HCChangeTypeRestrictionSelect aCtrl = new HCChangeTypeRestrictionSelect (new RequestField (sFieldCTR,
                                                                                                       aExistingObject == null ? null
                                                                                                                               : aExistingObject.getChangeTypeID ()),
                                                                                     aDisplayLocale);
      BootstrapFormHelper.markAsFormControl (aCtrl);
      BootstrapFormHelper.applyFormControlValidityState (aCtrl, aErrors);
      aCol2.addChild (aCtrl).addChild (BootstrapFormHelper.createDefaultErrorNode (aErrors, aDisplayLocale));
    }

    {
      final BootstrapCol aCol3 = aRow.createColumn (4);
      final String sFieldDesc = RequestParamMap.getFieldName (PREFIX_CHANGETYPE, sEntityID, SUFFIX_CHANGETYPE_DESC);
      final IErrorList aErrors = aFormErrors.getListOfField (sFieldDesc);
      final HCEdit aCtrl = new HCEdit (new RequestField (sFieldDesc,
                                                         aExistingObject == null ? null
                                                                                 : aExistingObject.getDescription ()));
      BootstrapFormHelper.markAsFormControl (aCtrl);
      BootstrapFormHelper.applyFormControlValidityState (aCtrl, aErrors);
      aCol3.addChild (aCtrl).addChild (BootstrapFormHelper.createDefaultErrorNode (aErrors, aDisplayLocale));
    }

    aRow.createColumn (2)
        .addChild (new BootstrapButton ().addChild ("Delete row")
                                         .setOnClick (JQuery.idRef (aRow).remove ())
                                         .setIcon (EDefaultIcon.DELETE)
                                         .setButtonType (EBootstrapButtonType.OUTLINE_DANGER));

    return aRow;
  }

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
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final CEDetails aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                final boolean bIsFormSubmitted,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    // TODO
    final HCNodeList aNL = new HCNodeList ();
    for (int i = 0; i < 5; ++i)
    {
      final IHCNode aRow = _createInputForm (aWPEC, null, null, aFormErrors);
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
