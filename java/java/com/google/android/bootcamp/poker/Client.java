// This file is part of the 'texasholdem' project, an open source
// Texas Hold'em poker application written in Java.
//
// Copyright 2009 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.bootcamp.poker;

import java.util.List;
import java.util.Set;

/**
 * A player client showing the table information and acting on behalf of the
 * player. <br />
 * <br />
 * 
 * Must be implemented by any client representing a player, either human or bot.
 * 
 * @author Oscar Stigter
 */
public interface Client {
    
    /**
     * Handles the start of a new hand.
     * The client should hide the current cards
     * and update the cast.
     * 
     * @param dealer
     *            The dealer.
     */
    void handStarted(int cash);
    
    /**
     * Sets the cards when a new 
     * @param cards
     */
    public void setCards(List<Card> cards);
    
    /**
     * Requests this player to act, selecting one of the allowed actions.
     * 
     * @param minBet
     *            The minimum bet.
     * @param currentBet
     *            The current bet.
     * @param allowedActions
     *            The allowed actions.
     * 
     * @return The selected action.
     */
    Action act(int minBet, int currentBet, Set<Action> allowedActions);
}
