package com.avatarduel.command;

import com.avatarduel.model.Game;
import com.avatarduel.model.card.CharacterCard;
import com.avatarduel.model.card.LandCard;
import com.avatarduel.model.player_component.Player;
import com.avatarduel.model.type.CardType;
import com.avatarduel.model.type.Phase;
import com.avatarduel.model.type.PlayerType;

public class PlayLandCardAction implements IAction {
    private PlayerType playerType;
    private int landCardID;

    public PlayLandCardAction(int idCard, PlayerType playerType) {
        this.playerType = playerType;
        this.landCardID = idCard;
    }

    @Override
    public void execute() {
        Player player = Game.getInstance().getPlayerByType(playerType);
        // remove card
        LandCard landCard = (LandCard) Game.getInstance().getPlayerByType(playerType).getHand().stream()
                .filter(card -> card.getId() == landCardID && card.getType().equals(CardType.LAND))
                .findFirst()
                .orElse(null);
        player.getHand().remove(landCard);
        // add power to player
        player.getPower().add(landCard.getElement(),1);
        // change state of hasPlayLand of player
        player.hasPlayLand = true;
    }

    @Override
    public boolean validate() {
        LandCard landCard = (LandCard) Game.getInstance().getPlayerByType(playerType).getHand().stream()
                .filter(card -> card.getId() == landCardID && card.getType().equals(CardType.LAND))
                .findFirst()
                .orElse(null);
        Phase currPhase = Game.getInstance().getCurrentPhase().getPhase();
        PlayerType currPlayer = Game.getInstance().getCurrentPlayer();
        int currentFieldSize = Game.getInstance().getPlayerByType(playerType).getField().getSkillCardList().size();

        return (((currPhase == Phase.MAIN1) || (currPhase == Phase.MAIN2))
                        && (currPlayer == playerType)
                        && (landCard != null)
                        && (CardType.LAND == landCard.getType())
                        && (currentFieldSize < Game.getInstance().getPlayerByType(playerType).getField().getFieldSize())
                        && !(Game.getInstance().getPlayerByType(currPlayer).hasPlayLand)
                );
    }
}
