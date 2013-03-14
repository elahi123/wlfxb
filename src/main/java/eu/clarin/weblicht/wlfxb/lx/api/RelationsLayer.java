/**
 * wlfxb - a library for creating and processing of TCF data streams.
 *
 * Copyright (C) Yana Panchenko.
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
package eu.clarin.weblicht.wlfxb.lx.api;

import java.util.List;

public interface RelationsLayer extends LexiconLayer {

    public int size();

    public Relation getRelation(int index);

    public Relation[] getRelations(Lemma lemma);

    public Lemma[] getLemmas(Relation relation);

    public String[] getWords(Relation relation);

    public Relation addRelation(String type, String function, Integer frequency, List<Term> terms);

    public Relation addRelation(String type, String function, Integer frequency, Sig sig, List<Term> terms);

    public Term createTerm(Lemma lemma);

    public Term createTerm(String word);

    public Sig createSig(String measure, double value);
}
