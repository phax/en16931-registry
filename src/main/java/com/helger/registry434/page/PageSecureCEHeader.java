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
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.URLHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.ext.HCA_MailTo;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCEM;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.html.jquery.JQuery;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAnonymousFunction;
import com.helger.html.jscode.JSAssocArray;
import com.helger.html.jscode.JSPackage;
import com.helger.html.jscode.JSVar;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.alert.BootstrapQuestionBox;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapFormHelper;
import com.helger.photon.bootstrap4.form.BootstrapViewForm;
import com.helger.photon.bootstrap4.grid.BootstrapCol;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.PhotonUnifiedResponse;
import com.helger.photon.core.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.core.app.context.ILayoutExecutionContext;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.html.select.HCCountrySelect;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.js.JSJQueryHelper;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.photon.uictrls.famfam.FamFamFlags;
import com.helger.registry434.app.MetaManager;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.EObjectStatus;
import com.helger.registry434.domain.EObjectType;
import com.helger.registry434.domain.ICEHeader;
import com.helger.registry434.ui.AbstractBSWebPageForm;
import com.helger.registry434.ui.AppCommonUI;
import com.helger.registry434.ui.HCObjectStatusSelect;
import com.helger.registry434.ui.HCObjectTypeSelect;
import com.helger.servlet.request.IRequestParamMap;
import com.helger.servlet.request.RequestParamMap;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * CE header management
 *
 * @author Philip Helger
 */
public class PageSecureCEHeader extends AbstractBSWebPageForm <ICEHeader>
{
  private static final String FIELD_NAME = "name";
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_COUNTRY = "country";
  private static final String FIELD_SECTOR = "sector";
  private static final String FIELD_PURPOSE = "purpose";
  private static final String FIELD_PUBLISHER = "publisher";
  private static final String FIELD_GOVERNOR = "governor";
  private static final String FIELD_UNDERLYING_SPEC = "underlyingspec";
  private static final String FIELD_FURTHER_INFO = "furtherinfo";
  private static final String PREFIX_EXTERNAL_URL = "externalurl";
  private static final String SUFFIX_EXTERNAL_URL = "url";
  private static final String FIELD_STATUS = "status";
  private static final String FIELD_CONTACT_NAME = "contactname";
  private static final String FIELD_CONTACT_EMAIL = "contactemail";

  private static final String TMP_ID_PREFIX = "tmp";

  private static final AjaxFunctionDeclaration s_aAjaxCreateURL;

  static
  {
    s_aAjaxCreateURL = addAjax ( (aRequestScope, aAjaxResponse) -> {
      final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
      final IHCNode aNode = _createURLInputForm (aLEC, (String) null, (String) null, new FormErrorList ());
      aAjaxResponse.html (aNode);
    });
  }

  @Nonnull
  private static IHCNode _createURLInputForm (@Nonnull final ILayoutExecutionContext aLEC,
                                              @Nullable final String sExistingObject,
                                              @Nullable final String sExistingID,
                                              @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sEntityID = StringHelper.hasText (sExistingID) ? sExistingID
                                                                : TMP_ID_PREFIX +
                                                                  Integer.toString (GlobalIDFactory.getNewIntID ());

    final BootstrapRow aRow = new BootstrapRow ().setID (sEntityID);

    {
      final BootstrapCol aCol1 = aRow.createColumn (10);
      final String sFieldURL = RequestParamMap.getFieldName (PREFIX_EXTERNAL_URL, sEntityID, SUFFIX_EXTERNAL_URL);
      final IErrorList aErrors = aFormErrors.getListOfField (sFieldURL);
      final HCEdit aCtrl = new HCEdit (new RequestField (sFieldURL, sExistingObject));
      BootstrapFormHelper.markAsFormControl (aCtrl);
      BootstrapFormHelper.applyFormControlValidityState (aCtrl, aErrors);
      aCol1.addChild (aCtrl).addChild (BootstrapFormHelper.createDefaultErrorNode (aErrors, aDisplayLocale));
    }

    aRow.createColumn (2)
        .addChild (new BootstrapButton ().addChild ("Delete row")
                                         .setOnClick (JQuery.idRef (aRow).remove ())
                                         .setIcon (EDefaultIcon.DELETE)
                                         .setButtonType (EBootstrapButtonType.OUTLINE_DANGER));

    return aRow;
  }

  public PageSecureCEHeader (@Nonnull @Nonempty final String sID)
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

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Type")
                                                     .setCtrl (aSelectedObject.getType ().getDisplayName ()));

    {
      IHCNode aCtrl;
      if (aSelectedObject.hasCountry ())
      {
        final String sCountryID = aSelectedObject.getCountry ();
        final Locale aCountry = CountryCache.getInstance ().getCountry (sCountryID);
        final String sCountry = aCountry == null ? sCountryID : aCountry.getDisplayCountry (aDisplayLocale);
        aCtrl = new HCSpan ().addChild (sCountry + " ").addChild (FamFamFlags.getFlagNodeFromLocale (aCountry));
      }
      else
        aCtrl = new HCEM ().addChild ("Country independent");
      aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Country").setCtrl (aCtrl));
    }

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Sector")
                                                     .setCtrl (aSelectedObject.hasSector () ? new HCTextNode (aSelectedObject.getSector ())
                                                                                            : new HCEM ().addChild ("Sector independent")));

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Purpose")
                                                     .setCtrl (HCExtHelper.nl2divList (aSelectedObject.getPurpose ())));

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Publisher").setCtrl (aSelectedObject.getPublisher ()));

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Governor").setCtrl (aSelectedObject.getGovernor ()));

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Underlying specification")
                                                     .setCtrl (aSelectedObject.getUnderlyingSpec ()));

    if (aSelectedObject.hasFurtherInfo ())
      aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Further information")
                                                       .setCtrl (HCExtHelper.nl2divList (aSelectedObject.getFurtherInfo ())));

    if (aSelectedObject.externalURLs ().isNotEmpty ())
    {
      final HCNodeList aCtrl = new HCNodeList ();
      for (final String sURL : aSelectedObject.externalURLs ())
        aCtrl.addChild (new HCDiv ().addChild (HCA.createLinkedWebsite (sURL)));
      aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("External URLs").setCtrl (aCtrl));
    }

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Status")
                                                     .setCtrl (AppCommonUI.getUIStatus (aSelectedObject.getStatus ())));

    aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact name")
                                                     .setCtrl (aSelectedObject.getContactName ()));

    if (aSelectedObject.hasContactEmail ())
      aViewForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact email address")
                                                       .setCtrl (HCA_MailTo.createLinkedEmail (aSelectedObject.getContactEmail ())));
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
                                                 .setHelpText ("A short descriptive name for general reference. Display name of the element."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Type")
                                                 .setCtrl (new HCObjectTypeSelect (new RequestField (FIELD_TYPE,
                                                                                                     aSelectedObject == null ? null
                                                                                                                             : aSelectedObject.getTypeID ()),
                                                                                   aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_TYPE)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Country")
                                                 .setCtrl (new HCCountrySelect (new RequestField (FIELD_COUNTRY,
                                                                                                  aSelectedObject == null ? null
                                                                                                                          : aSelectedObject.getCountry ()),
                                                                                aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_COUNTRY))
                                                 .setHelpText ("A country code, which the requirements the specification supports. No country selection means country independent."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Sector")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_SECTOR,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getSector ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SECTOR))
                                                 .setHelpText ("The industry sector, which requirements the specification supports (e.g. 'government' or 'energy'). If no sector is provided it is considered sector independent."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Purpose")
                                                 .setCtrl (new HCTextArea (new RequestField (FIELD_PURPOSE,
                                                                                             aSelectedObject == null ? null
                                                                                                                     : aSelectedObject.getPurpose ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_PURPOSE))
                                                 .setHelpText ("A brief description of what the specification is intended for."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Publisher")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_PUBLISHER,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getPublisher ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_PUBLISHER))
                                                 .setHelpText ("The party who formally publishes the specification."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Governor")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_GOVERNOR,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getGovernor ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_GOVERNOR))
                                                 .setHelpText ("The party who provides the specification its authority."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Underlying specification")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_UNDERLYING_SPEC,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getUnderlyingSpec ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_UNDERLYING_SPEC))
                                                 .setHelpText ("The specification that is used as base. A valid instance according to a CIUS specification must also be valid according to its underlying specification. If not it is an extension."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Further information")
                                                 .setCtrl (new HCTextArea (new RequestField (FIELD_FURTHER_INFO,
                                                                                             aSelectedObject == null ? null
                                                                                                                     : aSelectedObject.getFurtherInfo ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_FURTHER_INFO))
                                                 .setHelpText ("More detailed information about the specification."));

    {
      final HCDiv aEntityContainer = new HCDiv ().setID ("externalurls");

      final IRequestParamMap aParamBBs = aWPEC.getRequestParamMap ().getMap (PREFIX_EXTERNAL_URL);
      if (bIsFormSubmitted)
      {
        // Re-show of form
        if (aParamBBs != null)
          for (final String sEntityRowID : CollectionHelper.getSorted (aParamBBs.keySet ()))
            aEntityContainer.addChild (_createURLInputForm (aWPEC, null, sEntityRowID, aFormErrors));
      }
      else
      {
        if (aSelectedObject != null)
        {
          // add all existing stored entities
          for (final String aEntity : aSelectedObject.externalURLs ())
            aEntityContainer.addChild (_createURLInputForm (aWPEC, aEntity, (String) null, aFormErrors));
        }
      }

      final JSAnonymousFunction aJSAppend = new JSAnonymousFunction ();
      final JSVar aJSAppendData = aJSAppend.param ("data");
      aJSAppend.body ()
               .add (JQuery.idRef (aEntityContainer)
                           .append (aJSAppendData.ref (PhotonUnifiedResponse.HtmlHelper.PROPERTY_HTML)));

      final JSPackage aOnAdd = new JSPackage ();
      aOnAdd.add (new JQueryAjaxBuilder ().url (s_aAjaxCreateURL.getInvocationURL (aRequestScope))
                                          .data (new JSAssocArray ())
                                          .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aJSAppend, null))
                                          .build ());

      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addChild (new BootstrapButton ().addChild ("Add external URL")
                                               .setIcon (EDefaultIcon.PLUS)
                                               .setOnClick (aOnAdd)
                                               .setButtonType (EBootstrapButtonType.OUTLINE_SECONDARY));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("External URLs")
                                                   .setCtrl (aEntityContainer, aToolbar)
                                                   .setErrorList (aFormErrors.getListOfField (PREFIX_EXTERNAL_URL))
                                                   .setHelpText ("External links to more detailed information about the specification."));
    }

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Status")
                                                 .setCtrl (new HCObjectStatusSelect (new RequestField (FIELD_STATUS,
                                                                                                       aSelectedObject == null ? null
                                                                                                                               : aSelectedObject.getStatusID ()),
                                                                                     aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_STATUS))
                                                 .setHelpText ("The status of the specification."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Contact name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_CONTACT_NAME,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getContactName ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_CONTACT_NAME))
                                                 .setHelpText ("The name of the contact point for that specification."));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_CONTACT_EMAIL,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getContactEmail ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_CONTACT_EMAIL))
                                                 .setHelpText ("The email address of the contact point for that specification."));
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ICEHeader aSelectedObject,
                                                 @Nonnull final FormErrorList aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final boolean bEdit = eFormAction.isEdit ();
    final CEHeaderManager aCEHeaderMgr = MetaManager.getCEHeaderMgr ();

    final String sName = aWPEC.params ().getAsStringTrimmed (FIELD_NAME);

    final String sTypeID = aWPEC.params ().getAsStringTrimmed (FIELD_TYPE);
    final EObjectType eType = EObjectType.getFromIDOrNull (sTypeID);

    final String sCountryCode = aWPEC.params ().getAsStringTrimmed (FIELD_COUNTRY);
    final Locale aCountry = CountryCache.getInstance ().getCountry (sCountryCode);

    final String sSector = aWPEC.params ().getAsStringTrimmed (FIELD_SECTOR);

    final String sPurpose = aWPEC.params ().getAsStringTrimmed (FIELD_PURPOSE);

    final String sPublisher = aWPEC.params ().getAsStringTrimmed (FIELD_PUBLISHER);

    final String sGovernor = aWPEC.params ().getAsStringTrimmed (FIELD_GOVERNOR);

    final String sUnderlyingSpec = aWPEC.params ().getAsStringTrimmed (FIELD_UNDERLYING_SPEC);

    final String sFurtherInfo = aWPEC.params ().getAsStringTrimmed (FIELD_FURTHER_INFO);

    final ICommonsSet <String> aExternalURLs = new CommonsLinkedHashSet <> ();
    final IRequestParamMap aParamExternalURLs = aWPEC.getRequestParamMap ().getMap (PREFIX_EXTERNAL_URL);
    if (aParamExternalURLs != null)
      for (final String sEntityRowID : aParamExternalURLs.keySet ())
      {
        final ICommonsMap <String, String> aEntityRow = aParamExternalURLs.getValueMap (sEntityRowID);
        final int nErrors = aFormErrors.size ();

        // Type
        final String sFieldURL = RequestParamMap.getFieldName (PREFIX_EXTERNAL_URL, sEntityRowID, SUFFIX_EXTERNAL_URL);
        final String sURL = aEntityRow.get (SUFFIX_EXTERNAL_URL);

        if (StringHelper.hasNoText (sURL))
          aFormErrors.addFieldError (sFieldURL, "A URL must be provided.");
        else
          if (URLHelper.getAsURL (sURL, false) == null)
            aFormErrors.addFieldError (sFieldURL, "The provided URL is invalid.");

        if (aFormErrors.size () == nErrors)
        {
          // Add to set
          if (!aExternalURLs.add (sURL))
          {
            aFormErrors.addFieldError (sFieldURL, "Each URL may be contained only once!");
          }
        }
      }

    final String sStatusID = aWPEC.params ().getAsStringTrimmed (FIELD_STATUS);
    final EObjectStatus eStatus = EObjectStatus.getFromIDOrNull (sStatusID);

    final String sContactName = aWPEC.params ().getAsStringTrimmed (FIELD_CONTACT_NAME);

    final String sContactEmail = aWPEC.params ().getAsStringTrimmed (FIELD_CONTACT_EMAIL);

    // Validate
    if (StringHelper.hasNoText (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name must be provided.");

    if (eType == null)
      aFormErrors.addFieldError (FIELD_TYPE, "A type must be selected.");

    if (StringHelper.hasText (sCountryCode) && aCountry == null)
      aFormErrors.addFieldError (FIELD_COUNTRY, "A country must be selected.");

    if (StringHelper.hasNoText (sPurpose))
      aFormErrors.addFieldError (FIELD_PURPOSE, "A purpose must be provided.");

    if (StringHelper.hasNoText (sPublisher))
      aFormErrors.addFieldError (FIELD_PUBLISHER, "A publisher must be provided.");

    if (StringHelper.hasNoText (sGovernor))
      aFormErrors.addFieldError (FIELD_GOVERNOR, "A governor must be provided.");

    if (StringHelper.hasNoText (sUnderlyingSpec))
      aFormErrors.addFieldError (FIELD_UNDERLYING_SPEC, "An underlying specification must be provided.");

    if (false)
      if (aExternalURLs.isEmpty ())
        aFormErrors.addFieldError (PREFIX_EXTERNAL_URL, "At least one external URL must be provided");

    if (eStatus == null)
      aFormErrors.addFieldError (FIELD_STATUS, "A status must be selected.");

    if (StringHelper.hasNoText (sContactName))
      aFormErrors.addFieldError (FIELD_CONTACT_NAME, "A contact name must be provided.");

    if (StringHelper.hasText (sContactEmail))
    {
      if (!EmailAddressHelper.isValid (sContactEmail))
        aFormErrors.addFieldError (FIELD_CONTACT_EMAIL, "The contact email address is not a valid email address.");
    }

    if (aFormErrors.isEmpty ())
    {
      if (bEdit)
      {
        aCEHeaderMgr.updateCEHeader (aSelectedObject.getID (),
                                     sName,
                                     eType,
                                     aCountry == null ? null : aCountry.getCountry (),
                                     sSector,
                                     sPurpose,
                                     sPublisher,
                                     sGovernor,
                                     sUnderlyingSpec,
                                     sFurtherInfo,
                                     aExternalURLs.getCopyAsList (),
                                     eStatus,
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
                                     aCountry == null ? null : aCountry.getCountry (),
                                     sSector,
                                     sPurpose,
                                     sPublisher,
                                     sGovernor,
                                     sUnderlyingSpec,
                                     sFurtherInfo,
                                     aExternalURLs.getCopyAsList (),
                                     eStatus,
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
