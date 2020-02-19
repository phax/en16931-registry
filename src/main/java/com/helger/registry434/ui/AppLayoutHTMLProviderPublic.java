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
package com.helger.registry434.ui;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.traits.IHCBootstrap4Trait;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapPageRenderer;
import com.helger.photon.core.appid.RequestSettings;
import com.helger.photon.core.execcontext.ISimpleWebExecutionContext;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.html.AbstractSWECHTMLProvider;
import com.helger.photon.core.html.CLayout;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.uicore.html.HCCookieConsent;
import com.helger.registry434.app.CApp;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.forcedredirect.ForcedRedirectException;

/**
 * Main class for creating HTML output
 *
 * @author Philip Helger
 */
public class AppLayoutHTMLProviderPublic extends AbstractSWECHTMLProvider implements IHCBootstrap4Trait
{
  public static final AppLayoutHTMLProviderPublic INSTANCE = new AppLayoutHTMLProviderPublic ();

  private AppLayoutHTMLProviderPublic ()
  {}

  @Nonnull
  private IHCNode _getNavbar (@Nonnull final ISimpleWebExecutionContext aSWEC)
  {
    final ISimpleURL aLinkToStartPage = aSWEC.getLinkToMenuItem (aSWEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar ();
    aNavbar.addBrand (AppCommonUI.createLogoImg (), aLinkToStartPage);
    aNavbar.addBrand (span (CApp.APP_NAME).addClass (CApp.CSS_CLASS_LOGO1), aLinkToStartPage);

    final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();
    aToggleable.addChild (new BootstrapButton ().addClass (CBootstrapCSS.ML_AUTO)
                                                .addClass (CBootstrapCSS.MR_2)
                                                .addChild ("Goto manager")
                                                .setOnClick (LinkHelper.getURLWithContext (AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH +
                                                                                           "/")));
    return aNavbar;
  }

  @Override
  protected void fillBody (@Nonnull final ISimpleWebExecutionContext aSWEC,
                           @Nonnull final HCHtml aHtml) throws ForcedRedirectException
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aSWEC.getRequestScope ();
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
    aOuterContainer.addChild (_getNavbar (aLEC));

    // Breadcrumbs
    aOuterContainer.addChild (BootstrapPageRenderer.getBreadcrumb (aLEC));

    // Menu and page content
    aOuterContainer.addChild (BootstrapPageRenderer.getMenuAndPageNextToEachOther (aLEC));

    // Footer
    {
      final BootstrapContainer aDiv = new BootstrapContainer ().setFluid (true).setID (CLayout.LAYOUT_AREAID_FOOTER);
      aDiv.addChild (p (CApp.APP_NAME + " - a CEN/TC 434 service"));
      aDiv.addChild (p ("Source code is available on ").addChild (new HCA (new SimpleURL ("https://github.com/phax/registry434/")).addChild ("GitHub")
                                                                                                                                  .setTargetBlank ())
                                                       .addChild (" - feel final free to contribute"));
      aOuterContainer.addChild (aDiv);
    }

    aOuterContainer.addChild (HCCookieConsent.createBottomDefault ("#000", "#0f0", "#0f0", null));

    aBody.addChild (aOuterContainer);
  }
}
