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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.alert.BootstrapQuestionBox;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapViewForm;
import com.helger.photon.bootstrap4.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.EObjectStatus;
import com.helger.registry434.domain.EObjectType;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AbstractBSWebPageForm;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * CE header management
 *
 * @author Philip Helger
 */
public class PageSecureCEHeaderList extends AbstractBSWebPageForm <ICEHeader>
{
  private static final String FIELD_NAME = "name";

  public PageSecureCEHeaderList (@Nonnull @Nonempty final String sID)
  {
    super (sID, "CIUS and Extensions");
    setDeleteHandler (new AbstractBootstrapWebPageActionHandlerDelete <ICEHeader, WebPageExecutionContext> ()
    {
      @Override
      protected void showDeleteQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                      @Nonnull final BootstrapForm aForm,
                                      @Nonnull final ICEHeader aSelectedObject)
      {
        aForm.addChild (new BootstrapQuestionBox ().addChild (new HCDiv ().addChild ("Are you sure to delete the element '" +
                                                                                     aSelectedObject.getName () +
                                                                                     "'?"))
                                                   .addChild (new HCDiv ().addChild ("It will NOT be possible to restore this element.")));
      }

      @Override
      protected void performDelete (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final ICEHeader aSelectedObject)
      {
        final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();
        if (aCEHeaderMgr.deleteCEHeader (aSelectedObject.getID ()).isChanged ())
          aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("The element '" +
                                                                              aSelectedObject.getName () +
                                                                              "' was successfully deleted!"));
        else
          aWPEC.postRedirectGetInternal (new BootstrapErrorBox ().addChild ("Error deleting element '" +
                                                                            aSelectedObject.getName () +
                                                                            "'!"));
      }
    });
  }

  @Override
  protected boolean isActionAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final EWebPageFormAction eFormAction,
                                     @Nullable final ICEHeader aSelectedObject)
  {
    if (eFormAction.isDelete ())
    {
      if (aSelectedObject == null || aSelectedObject.isDeleted ())
        return false;
    }

    return super.isActionAllowed (aWPEC, eFormAction, aSelectedObject);
  }

  @Override
  protected ICEHeader getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final CEHeaderManager aBBMgr = MetaManager.getCEHeaderMgr ();
    return aBBMgr.getCEHeaderOfID (sID);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final ICEHeader aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final BootstrapViewForm aViewForm = new BootstrapViewForm ();
    aNodeList.addChild (aViewForm);

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Name").setCtrl (aSelectedObject.getName ()));
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final ICEHeader aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                final boolean bIsFormSubmitted,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_NAME,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getName ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_NAME))
                                                 .setHelpText ("Display name of the element."));
  }

  @Override
  protected void validateAndSaveInputParameters (final WebPageExecutionContext aWPEC,
                                                 final ICEHeader aSelectedObject,
                                                 final FormErrorList aFormErrors,
                                                 final EWebPageFormAction eFormAction)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final boolean bEdit = eFormAction.isEdit ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    final String sName = aWPEC.params ().getAsStringTrimmed (FIELD_NAME);

    if (StringHelper.hasNoText (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name must be provided.");

    if (aFormErrors.isEmpty ())
    {
      // TODO
      final EObjectType eType = null;
      final String sCountry = null;
      final String sSector = null;
      final String sPurpose = null;
      final String sPublisher = null;
      final String sGovernor = null;
      final String sUnderlyingSpec = null;
      final String sFurtherInfo = null;
      final EObjectStatus eState = null;
      final String sContactName = null;
      final String sContactEmail = null;

      if (bEdit)
      {
        aCEHeaderMgr.updateCEHeader (aSelectedObject.getID (),
                                     sName,
                                     eType,
                                     sCountry,
                                     sSector,
                                     sPurpose,
                                     sPublisher,
                                     sGovernor,
                                     sUnderlyingSpec,
                                     sFurtherInfo,
                                     eState,
                                     sContactName,
                                     sContactEmail);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully edited '" +
                                                                            aSelectedObject.getName () +
                                                                            "'."));
      }
      else
      {
        aCEHeaderMgr.createCEHeader (sName,
                                     eType,
                                     sCountry,
                                     sSector,
                                     sPurpose,
                                     sPublisher,
                                     sGovernor,
                                     sUnderlyingSpec,
                                     sFurtherInfo,
                                     eState,
                                     sContactName,
                                     sContactEmail);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully created '" + sName + "'."));
      }
    }
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    {
      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addChild (new BootstrapButton ().addChild ("Create a new element")
                                               .setOnClick (createCreateURL (aWPEC))
                                               .setIcon (EDefaultIcon.NEW)
                                               .setButtonType (EBootstrapButtonType.OUTLINE_PRIMARY));
      aNodeList.addChild (aToolbar);
    }

    final HCTable aTable = new HCTable (new DTCol ("").setVisible (false),
                                        new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());
    for (final ICEHeader aItem : aCEHeaderMgr.getAll ())
    {
      final ISimpleURL aViewURL = createViewURL (aWPEC, aItem);
      final HCRow aRow = aTable.addBodyRow ();

      aRow.addCell (aItem.getID ());
      aRow.addCell (new HCA (aViewURL).addChild (aItem.getName ()));

      final IHCCell <?> aActionCell = aRow.addCell ();
      if (isActionAllowed (aWPEC, EWebPageFormAction.EDIT, aItem))
        aActionCell.addChild (createEditLink (aWPEC, aItem, "Edit"));
      else
        aActionCell.addChild (createEmptyAction ());
      aActionCell.addChild (" ");
      if (isActionAllowed (aWPEC, EWebPageFormAction.COPY, aItem))
        aActionCell.addChild (createCopyLink (aWPEC, aItem, "Copy"));
      else
        aActionCell.addChild (createEmptyAction ());
      aActionCell.addChild (" ");
      if (isActionAllowed (aWPEC, EWebPageFormAction.DELETE, aItem))
        aActionCell.addChild (createDeleteLink (aWPEC, aItem, "Delete"));
      else
        aActionCell.addChild (createEmptyAction ());
    }
    aNodeList.addChild (aTable);
    aNodeList.addChild (BootstrapDataTables.createDefaultDataTables (aWPEC, aTable));
  }
}
