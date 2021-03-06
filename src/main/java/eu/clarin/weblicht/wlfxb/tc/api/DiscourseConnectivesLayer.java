/**
 * wlfxb - a library for creating and processing of TCF data streams.
 *
 * Copyright (C) University of Tübingen.
 *
 * This file is part of wlfxb.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.weblicht.wlfxb.tc.api;

import java.util.List;

/**
 * The <tt>DiscourseConnectivesLayer</tt> layer annotates discourse connectives. 
 * For each discourse connective its type can be specified. In such a case, the 
 * tagset used for discourse connectives types should be specified on the layer 
 * level.
 * 
 * @author Yana Panchenko
 */
public interface DiscourseConnectivesLayer extends TextCorpusLayer {

    public String getTypesTagset();

    public DiscourseConnective getConnective(int index);

    public DiscourseConnective getConnective(Token token);

    public Token[] getTokens(DiscourseConnective connective);

    public DiscourseConnective addConnective(List<Token> tokens);

    public DiscourseConnective addConnective(List<Token> tokens, String semanticType);
}
