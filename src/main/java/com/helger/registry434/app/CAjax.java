/**
 * Copyright (C) 2019-2021 Philip Helger
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
package com.helger.registry434.app;

import javax.annotation.concurrent.Immutable;

import com.helger.photon.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.ajax.decl.IAjaxFunctionDeclaration;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTablesI18N;

/**
 * This class defines the available ajax functions
 *
 * @author Philip Helger
 */
@Immutable
public final class CAjax
{
  public static final IAjaxFunctionDeclaration DATATABLES = AjaxFunctionDeclaration.builder ("dataTables")
                                                                                   .executor (AjaxExecutorDataTables.class)
                                                                                   .build ();
  public static final IAjaxFunctionDeclaration DATATABLES_I18N = AjaxFunctionDeclaration.builder ("datatables-i18n")
                                                                                        .executor (new AjaxExecutorDataTablesI18N (CApp.DEFAULT_LOCALE))
                                                                                        .build ();

  private CAjax ()
  {}
}
