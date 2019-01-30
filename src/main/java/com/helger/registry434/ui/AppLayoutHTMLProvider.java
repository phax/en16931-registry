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
package com.helger.registry434.ui;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.basic.app.appid.CApplicationID;
import com.helger.photon.basic.app.appid.RequestSettings;
import com.helger.photon.basic.app.menu.IMenuItemPage;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapPageRenderer;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.app.context.ISimpleWebExecutionContext;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.context.SimpleWebExecutionContext;
import com.helger.photon.core.app.html.AbstractSWECHTMLProvider;
import com.helger.photon.core.app.layout.CLayout;
import com.helger.photon.core.servlet.LogoutServlet;
import com.helger.photon.core.url.LinkHelper;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.util.SecurityHelper;
import com.helger.photon.uicore.html.HCCookieConsent;
import com.helger.registry434.app.CApp;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.forcedredirect.ForcedRedirectException;

/**
 * Main class for creating HTML output
 *
 * @author Philip Helger
 */
public class AppLayoutHTMLProvider extends AbstractSWECHTMLProvider
{
  public static final AppLayoutHTMLProvider INSTANCE = new AppLayoutHTMLProvider ();

  private AppLayoutHTMLProvider ()
  {}

  @Nonnull
  private static IHCNode _getNavbar (@Nonnull final SimpleWebExecutionContext aSWEC, final boolean bIsAdministration)
  {
    final Locale aDisplayLocale = aSWEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aSWEC.getRequestScope ();

    final ISimpleURL aLinkToStartPage = aSWEC.getLinkToMenuItem (aSWEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar ();

    aNavbar.addBrand (AppCommonUI.createLogoImg (), aLinkToStartPage);

    aNavbar.addBrand (new HCNodeList ().addChild (new HCSpan ().addChild (CApp.APP_NAME)
                                                               .addClass (CApp.CSS_CLASS_LOGO1))
                                       .addChild (bIsAdministration ? new HCSpan ().addChild (" Administration")
                                                                                   .addClass (CApp.CSS_CLASS_LOGO2)
                                                                    : null),
                      aLinkToStartPage);

    final IUser aUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    if (aUser != null)
    {
      final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();
      aToggleable.addAndReturnText ()
                 .addChild (new HCSpan ().addChild ("Logged in as ")
                                         .addChild (new HCStrong ().addChild (SecurityHelper.getUserDisplayName (aUser,
                                                                                                                 aDisplayLocale))))
                 .addClass (CBootstrapCSS.ML_AUTO);
      aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (aRequestScope,
                                                                                             LogoutServlet.SERVLET_DEFAULT_PATH))
                                                  .addChild (EPhotonCoreText.LOGIN_LOGOUT.getDisplayText (aDisplayLocale))
                                                  .addClass (CBootstrapCSS.ML_2)
                                                  .addClass (CBootstrapCSS.BTN_OUTLINE_INFO));
    }
    return aNavbar;
  }

  @Override
  protected void fillBody (@Nonnull final ISimpleWebExecutionContext aSWEC,
                           @Nonnull final HCHtml aHtml) throws ForcedRedirectException
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aSWEC.getRequestScope ();
    final boolean bIsAdministration = CApplicationID.APP_ID_SECURE.equals (RequestSettings.getApplicationID (aRequestScope));
    final Locale aDisplayLocale = aSWEC.getDisplayLocale ();
    final IMenuItemPage aMenuItem = RequestSettings.getMenuItem (aRequestScope);
    final LayoutExecutionContext aLEC = new LayoutExecutionContext (aSWEC, aMenuItem);
    final HCHead aHead = aHtml.head ();
    final HCBody aBody = aHtml.body ();

    // Add menu item in page title
    aHead.setPageTitle (StringHelper.getConcatenatedOnDemand (CApp.APP_NAME,
                                                              " - ",
                                                              aMenuItem.getDisplayText (aDisplayLocale)));

    final BootstrapContainer aOuterContainer = new BootstrapContainer ().setFluid (true);

    // Header
    aOuterContainer.addChild (_getNavbar (aLEC, bIsAdministration));

    // Breadcrumbs
    aOuterContainer.addChild (BootstrapPageRenderer.getBreadcrumb (aLEC));

    // Menu and page content
    aOuterContainer.addChild (BootstrapPageRenderer.getMenuAndPageNextToEachOther (aLEC));

    // Footer
    {
      final BootstrapContainer aDiv = new BootstrapContainer ().setFluid (true).setID (CLayout.LAYOUT_AREAID_FOOTER);
      aDiv.addChild (new HCP ().addChild (CApp.APP_NAME + " - a CEN/TC 434 service"));
      aOuterContainer.addChild (aDiv);
    }

    aOuterContainer.addChild (HCCookieConsent.createBottomDefault ("#000", "#0f0", "#0f0", null));

    aBody.addChild (aOuterContainer);
  }
}
