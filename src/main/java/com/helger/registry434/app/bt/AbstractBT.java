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
package com.helger.registry434.app.bt;

import java.io.Serializable;

import javax.annotation.Nullable;

public abstract class AbstractBT implements Serializable
{
  private final BusinessGroup m_aParent;
  private final String m_sID;
  private final String m_sName;
  private final String m_sCard;

  public AbstractBT (@Nullable final BusinessGroup aParent, final String sID, final String sName, final String sCard)
  {
    m_aParent = aParent;
    m_sID = sID;
    m_sName = sName;
    m_sCard = sCard;
  }

  @Nullable
  public BusinessGroup getParent ()
  {
    return m_aParent;
  }

  public boolean hasParent ()
  {
    return m_aParent != null;
  }

  public String getID ()
  {
    return m_sID;
  }

  public String getName ()
  {
    return m_sName;
  }

  public String getCard ()
  {
    return m_sCard;
  }
}
