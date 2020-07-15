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
package com.helger.registry434.servlet;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import com.helger.commons.vendor.VendorInfo;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAssocArray;
import com.helger.photon.ajax.IAjaxRegistry;
import com.helger.photon.bootstrap4.ext.BootstrapSystemMessage;
import com.helger.photon.bootstrap4.servlet.WebAppListenerBootstrap;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.locale.ILocaleManager;
import com.helger.photon.core.menu.MenuTree;
import com.helger.photon.core.requestparam.RequestParameterHandlerURLPathNamed;
import com.helger.photon.core.requestparam.RequestParameterManager;
import com.helger.photon.uictrls.datatables.DataTablesLengthMenu;
import com.helger.photon.uictrls.datatables.EDataTablesFilterType;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTablesI18N;
import com.helger.photon.uictrls.datatables.plugins.DataTablesPluginSearchHighlight;
import com.helger.registry434.app.AppDefaultSecurity;
import com.helger.registry434.app.AppInternalErrorHandler;
import com.helger.registry434.app.AppMenuPublic;
import com.helger.registry434.app.AppMenuSecure;
import com.helger.registry434.app.AppSettings;
import com.helger.registry434.app.CAjax;
import com.helger.registry434.app.CApp;
import com.helger.registry434.app.MetaManager;
import com.helger.scope.singleton.SingletonHelper;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * Callbacks for the application server
 *
 * @author Philip Helger
 */
public final class AppWebAppListener extends WebAppListenerBootstrap
{
  private static final DataTablesLengthMenu LENGTH_MENU = new DataTablesLengthMenu ().addItem (25).addItem (50).addItem (100).addItemAll ();

  @Override
  protected String getInitParameterDebug (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getGlobalDebug ();
  }

  @Override
  protected String getInitParameterProduction (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getGlobalProduction ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getDataPath ();
  }

  @Override
  protected boolean shouldCheckFileAccess (@Nonnull final ServletContext aSC)
  {
    return AppSettings.isCheckFileAccess ();
  }

  @Override
  protected void initGlobalSettings ()
  {
    // Internal stuff:
    VendorInfo.setInceptionYear (2014);

    // Avoid startup error logs
    SingletonHelper.setDebugConsistency (false);
  }

  @Override
  protected void initLocales (@Nonnull final ILocaleManager aLocaleMgr)
  {
    aLocaleMgr.registerLocale (CApp.DEFAULT_LOCALE);
    aLocaleMgr.setDefaultLocale (CApp.DEFAULT_LOCALE);
  }

  @Override
  protected void initAjax (@Nonnull final IAjaxRegistry aAjaxInvoker)
  {
    aAjaxInvoker.registerFunction (CAjax.DATATABLES);
    aAjaxInvoker.registerFunction (CAjax.DATATABLES_I18N);
  }

  @Override
  protected void initMenu ()
  {
    // Create all menu items
    {
      final MenuTree aMenuTree = new MenuTree ();
      AppMenuSecure.init (aMenuTree);
      PhotonGlobalState.state (CApplicationID.APP_ID_SECURE).setMenuTree (aMenuTree);
    }
    {
      final MenuTree aMenuTree = new MenuTree ();
      AppMenuPublic.init (aMenuTree);
      PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC).setMenuTree (aMenuTree);
    }
  }

  @Override
  protected void initSecurity ()
  {
    // Set all security related stuff
    AppDefaultSecurity.init ();
  }

  @Override
  protected void initUI ()
  {
    // UI stuff
    BootstrapDataTables.setConfigurator ( (aLEC, aTable, aDataTables) -> {
      final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
      aDataTables.setAutoWidth (false)
                 .setLengthMenu (LENGTH_MENU)
                 .setAjaxBuilder (new JQueryAjaxBuilder ().url (CAjax.DATATABLES.getInvocationURL (aRequestScope))
                                                          .data (new JSAssocArray ().add (AjaxExecutorDataTables.OBJECT_ID,
                                                                                          aTable.getID ())))
                 .setServerFilterType (EDataTablesFilterType.ALL_TERMS_PER_ROW)
                 .setTextLoadingURL (CAjax.DATATABLES_I18N.getInvocationURL (aRequestScope), AjaxExecutorDataTablesI18N.LANGUAGE_ID)
                 .addPlugin (new DataTablesPluginSearchHighlight ());
    });

    // By default allow markdown in system message
    BootstrapSystemMessage.setDefaultUseMarkdown (true);
  }

  @Override
  protected void initManagers ()
  {
    RequestParameterManager.getInstance ().setParameterHandler (new RequestParameterHandlerURLPathNamed ());
    AppInternalErrorHandler.doSetup ();
    // Ensure the instance is present
    MetaManager.getInstance ();
  }

  @Override
  protected void initJobs ()
  {}
}
