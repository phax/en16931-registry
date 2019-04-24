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
package com.helger.registry434.page;

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
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
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAnonymousFunction;
import com.helger.html.jscode.JSAssocArray;
import com.helger.html.jscode.JSPackage;
import com.helger.html.jscode.JSVar;
import com.helger.photon.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.alert.BootstrapQuestionBox;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapFormHelper;
import com.helger.photon.bootstrap4.grid.BootstrapCol;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.execcontext.ILayoutExecutionContext;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.html.formlabel.HCFormLabel;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.js.JSJQueryHelper;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.CEHeaderManagerExt;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.app.bt.BTManager;
import com.helger.registry434.app.bt.BusinessTerm;
import com.helger.registry434.domain.CEDetailsItem;
import com.helger.registry434.domain.CEDetailsList;
import com.helger.registry434.domain.EChangeTypeRestriction;
import com.helger.registry434.domain.EObjectType;
import com.helger.registry434.domain.ICEDetailsItem;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AbstractAppWebPageForm;
import com.helger.registry434.ui.AppCommonUI;
import com.helger.registry434.ui.HCBTSelect;
import com.helger.registry434.ui.HCChangeTypeRestrictionSelect;
import com.helger.servlet.request.IRequestParamMap;
import com.helger.servlet.request.RequestParamMap;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public class PageSecureCIUSDetails extends AbstractAppWebPageForm <ICEHeader>
{
  private static final String PREFIX_DETAILSITEM = "detailsitem";
  private static final String SUFFIX_DETAILSITEM_BT = "bt";
  private static final String SUFFIX_DETAILSITEM_CTR = "ctr";
  private static final String SUFFIX_DETAILSITEM_DESC = "desc";

  private static final AjaxFunctionDeclaration s_aAjaxAddRowChangeType;

  static
  {
    s_aAjaxAddRowChangeType = addAjax ( (aRequestScope, aAjaxResponse) -> {
      final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
      final IHCNode aNode = _createInputForm (aLEC, (ICEDetailsItem) null, (String) null, new FormErrorList ());
      aAjaxResponse.html (aNode);
    });
  }

  @Nonnull
  private static IHCNode _createInputForm (@Nonnull final ILayoutExecutionContext aLEC,
                                           @Nullable final ICEDetailsItem aExistingObject,
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
      final String sFieldBT = RequestParamMap.getFieldName (PREFIX_DETAILSITEM, sEntityID, SUFFIX_DETAILSITEM_BT);
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
      final String sFieldCTR = RequestParamMap.getFieldName (PREFIX_DETAILSITEM, sEntityID, SUFFIX_DETAILSITEM_CTR);
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
      final String sFieldDesc = RequestParamMap.getFieldName (PREFIX_DETAILSITEM, sEntityID, SUFFIX_DETAILSITEM_DESC);
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

  public PageSecureCIUSDetails (final String sID)
  {
    super (sID, "CIUS Details");
    setDeleteHandler (new AbstractBootstrapWebPageActionHandlerDelete <ICEHeader, WebPageExecutionContext> ()
    {
      @Override
      protected void showDeleteQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                      @Nonnull final BootstrapForm aForm,
                                      @Nonnull final ICEHeader aSelectedObject)
      {
        aForm.addChild (new BootstrapQuestionBox ().addChild (new HCDiv ().addChild ("Are you sure to delete the details of '" +
                                                                                     aSelectedObject.getName () +
                                                                                     "'?"))
                                                   .addChild (new HCDiv ().addChild ("It will NOT be possible to restore the details.")));
      }

      @Override
      protected void performDelete (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final ICEHeader aSelectedObject)
      {
        final CEHeaderManagerExt aCEHeaderMgr = MetaManager.getCEHeaderMgr ();
        if (aCEHeaderMgr.setDetails (aSelectedObject, null).isChanged ())
          aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("The details of '" +
                                                                              aSelectedObject.getName () +
                                                                              "' were successfully deleted!"));
        else
          aWPEC.postRedirectGetInternal (new BootstrapErrorBox ().addChild ("Error deleting details of '" +
                                                                            aSelectedObject.getName () +
                                                                            "'!"));
      }
    });
  }

  @Override
  protected ICEHeader getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, final String sID)
  {
    final CEHeaderManagerExt aCEMgr = MetaManager.getCEHeaderMgr ();
    return aCEMgr.getCEHeaderOfID (sID);
  }

  @Override
  protected boolean isActionAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final EWebPageFormAction eFormAction,
                                     @Nullable final ICEHeader aSelectedObject)
  {
    // A valid CIUS must be selected
    if (aSelectedObject == null || aSelectedObject.getType () != EObjectType.CIUS)
      return false;

    switch (eFormAction)
    {
      case VIEW:
      case EDIT:
      case COPY:
      case DELETE:
      case UNDELETE:
        if (!aSelectedObject.hasDetails ())
          return false;
        break;
    }

    return super.isActionAllowed (aWPEC, eFormAction, aSelectedObject);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final ICEHeader aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (AppCommonUI.createDetailsTable (aSelectedObject));
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nonnull final ICEHeader aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                final boolean bIsFormSubmitted,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();
    final boolean bEdit = eFormAction.isEdit ();

    aForm.addChild (getUIHandler ().createActionHeader (bEdit ? "Edit the details of '" +
                                                                aSelectedObject.getName () +
                                                                "'"
                                                              : "Create new details for '" +
                                                                aSelectedObject.getName () +
                                                                "'"));

    {
      final BootstrapRow aHeaderRow = new BootstrapRow ();
      {
        aHeaderRow.createColumn (3).addChild (HCFormLabel.createMandatory ("Business Term"));
        aHeaderRow.createColumn (3).addChild (HCFormLabel.createMandatory ("Change Type"));
        aHeaderRow.createColumn (4).addChild (HCFormLabel.createOptional ("Description"));
      }

      final HCDiv aEntityContainer = new HCDiv ().setID ("changes");

      if (bIsFormSubmitted)
      {
        // Re-show of form
        final IRequestParamMap aParamDetailsItems = aWPEC.getRequestParamMap ().getMap (PREFIX_DETAILSITEM);
        if (aParamDetailsItems != null)
          for (final String sEntityRowID : CollectionHelper.getSorted (aParamDetailsItems.keySet ()))
            aEntityContainer.addChild (_createInputForm (aWPEC, null, sEntityRowID, aFormErrors));
      }
      else
      {
        if (aSelectedObject.hasDetails ())
        {
          // add all existing stored entities
          for (final ICEDetailsItem aEntity : aSelectedObject.getDetails ().changes ())
            aEntityContainer.addChild (_createInputForm (aWPEC, aEntity, (String) null, aFormErrors));
        }
      }

      final JSAnonymousFunction aJSAppend = new JSAnonymousFunction ();
      final JSVar aJSAppendData = aJSAppend.param ("data");
      aJSAppend.body ()
               .add (JQuery.idRef (aEntityContainer)
                           .append (aJSAppendData.ref (PhotonUnifiedResponse.HtmlHelper.PROPERTY_HTML)));

      final JSPackage aOnAdd = new JSPackage ();
      aOnAdd.add (new JQueryAjaxBuilder ().url (s_aAjaxAddRowChangeType.getInvocationURL (aRequestScope))
                                          .data (new JSAssocArray ())
                                          .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aJSAppend, null))
                                          .build ());

      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addChild (new BootstrapButton ().addChild ("Add restriction")
                                               .setIcon (EDefaultIcon.PLUS)
                                               .setOnClick (aOnAdd)
                                               .setButtonType (EBootstrapButtonType.OUTLINE_SECONDARY));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Restrictions")
                                                   .setCtrl (aHeaderRow, aEntityContainer, aToolbar)
                                                   .setErrorList (aFormErrors.getListOfField (PREFIX_DETAILSITEM)));
    }
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nonnull final ICEHeader aSelectedObject,
                                                 @Nonnull final FormErrorList aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final boolean bEdit = eFormAction.isEdit ();
    final CEHeaderManagerExt aCEMgr = MetaManager.getCEHeaderMgr ();

    final ICommonsList <ICEDetailsItem> aDetailsItems = new CommonsArrayList <> ();
    final IRequestParamMap aParamDetailItems = aWPEC.getRequestParamMap ().getMap (PREFIX_DETAILSITEM);
    if (aParamDetailItems != null)
      for (final String sEntityRowID : aParamDetailItems.keySet ()
                                                        .getSorted (Comparator.comparingInt (AbstractAppWebPageForm::getAsIntAfterPrefix)))
      {
        final ICommonsMap <String, String> aEntityRow = aParamDetailItems.getValueMap (sEntityRowID);
        final int nErrors = aFormErrors.size ();

        // Business Term
        final String sFieldBT = RequestParamMap.getFieldName (PREFIX_DETAILSITEM, sEntityRowID, SUFFIX_DETAILSITEM_BT);
        final String sEntityBT = aEntityRow.get (SUFFIX_DETAILSITEM_BT);
        final BusinessTerm aEntityBT = BTManager.findBT (sEntityBT);
        if (StringHelper.hasNoText (sEntityBT))
          aFormErrors.addFieldError (sFieldBT, "A Business Term must be selected.");
        else
          if (aEntityBT == null)
            aFormErrors.addFieldError (sFieldBT, "The selected Business Term does not exist.");

        // Change type
        final String sFieldCTR = RequestParamMap.getFieldName (PREFIX_DETAILSITEM,
                                                               sEntityRowID,
                                                               SUFFIX_DETAILSITEM_CTR);
        final String sEntityCTRID = aEntityRow.get (SUFFIX_DETAILSITEM_CTR);
        final EChangeTypeRestriction eEntityCTR = EChangeTypeRestriction.getFromIDOrNull (sEntityCTRID);
        if (StringHelper.hasNoText (sEntityCTRID))
          aFormErrors.addFieldError (sFieldCTR, "A Change Type must be selected.");
        else
          if (eEntityCTR == null)
            aFormErrors.addFieldError (sFieldCTR, "The selected Change Type does not exist");

        // Description
        final String sEntityDesc = aEntityRow.get (SUFFIX_DETAILSITEM_DESC);

        if (aFormErrors.size () == nErrors)
        {
          // Add to list
          final CEDetailsItem aEntity = new CEDetailsItem (aEntityBT.getID (), eEntityCTR, sEntityDesc);
          aDetailsItems.add (aEntity);
        }
      }

    if (aDetailsItems.isEmpty ())
      aFormErrors.addFieldError (PREFIX_DETAILSITEM, "At least one restriction must be present.");

    if (aFormErrors.isEmpty ())
    {
      final CEDetailsList aDetailsList = new CEDetailsList (aDetailsItems);

      if (bEdit)
      {
        // edit
        aCEMgr.setDetails (aSelectedObject, aDetailsList);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully edited the details of '" +
                                                                            aSelectedObject.getName () +
                                                                            "'."));
      }
      else
      {
        // created
        aCEMgr.setDetails (aSelectedObject, aDetailsList);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully created the details for '" +
                                                                            aSelectedObject.getName () +
                                                                            "'."));
      }
    }
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CEHeaderManagerExt aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    final HCTable aTable = new HCTable (new DTCol ("").setVisible (false),
                                        new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Type"),
                                        new DTCol ("Details?"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());
    for (final ICEHeader aItem : aCEHeaderMgr.getAll (x -> x.getType () == EObjectType.CIUS))
    {
      final ISimpleURL aViewURL = createViewURL (aWPEC, aItem);
      final HCRow aRow = aTable.addBodyRow ();

      aRow.addCell (aItem.getID ());
      aRow.addCell (new HCA (aViewURL).addChild (aItem.getName ()));
      aRow.addCell (aItem.getType ().getDisplayName ());
      aRow.addCell (EPhotonCoreText.getYesOrNo (aItem.hasDetails (), aDisplayLocale));

      final IHCCell <?> aActionCell = aRow.addCell ();
      if (aItem.hasDetails ())
      {
        aActionCell.addChild (createEditLink (aWPEC, aItem, "Edit details"));
        aActionCell.addChild (createDeleteLink (aWPEC, aItem, "Delete details"));
      }
      else
      {
        aActionCell.addChild (createNestedCreateLink (aWPEC, aItem, "Create details"));
      }
    }
    aNodeList.addChild (aTable);
    aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
  }
}
