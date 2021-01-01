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
package com.helger.registry434.app.bt;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.name.IHasDisplayName;

public abstract class AbstractBT implements Serializable, IHasID <String>, IHasDisplayName
{
  private final BusinessGroup m_aParent;
  private final String m_sID;
  private final String m_sName;
  private final String m_sCard;

  public AbstractBT (@Nullable final BusinessGroup aParent,
                     @Nonnull @Nonempty final String sID,
                     @Nonnull @Nonempty final String sName,
                     @Nonnull @Nonempty final String sCard)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notEmpty (sCard, "Card");
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

  @Nonnull
  @Nonempty
  public final String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  @Nonnull
  @Nonempty
  public final String getCard ()
  {
    return m_sCard;
  }

  public final void forAllParents (@Nonnull final Consumer <String> aNameConsumer)
  {
    AbstractBT aCur = getParent ();
    while (aCur != null)
    {
      aNameConsumer.accept (aCur.getDisplayName ());
      aCur = aCur.getParent ();
    }
  }

  @Nonnull
  @Nonempty
  public final String getRecursiveDisplayName (@Nonnull final String sSeparator)
  {
    final StringBuilder aSB = new StringBuilder (getDisplayName ());
    forAllParents (x -> aSB.insert (0, sSeparator).insert (0, x));
    return aSB.toString ();
  }
}
