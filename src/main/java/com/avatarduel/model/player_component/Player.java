package com.avatarduel.model.player_component;

import com.avatarduel.exception.InvalidOperationException;
import com.avatarduel.model.card.*;
import com.avatarduel.model.type.CardType;
import com.avatarduel.model.type.CharacterState;
import com.avatarduel.model.type.PlayerType;

import java.util.EmptyStackException;

public class Player {
    protected Deck deck;
    protected Field field;
    protected Hand hand;
    protected PlayerType type;
    protected PowerManager power;
    protected int healthPoint;

    // hard coded constant
    private static final int firstDraw = 7; // can be better coded, but right now, still hardcoded
    private static final int deckSize = 60;
    private static final int fieldSize = 6;
    private static final int initialHP = 80;

    public Player(PlayerType type) {
        this.deck = new Deck(deckSize); // decksize harus lebih dari 7
        this.field = new Field(fieldSize);
        this.hand = new Hand(10); // hand size harus lebih dari 7 maksimum
        this.type = type;
        this.power = new PowerManager();
        this.healthPoint = initialHP; // starting health point, bisa dinamik namun sekarang statik saja
    }

    public void startGameDraw() throws EmptyStackException {
        for(int i = 0; i < firstDraw; i++){
            draw();
        }
    }

    public void draw() throws EmptyStackException {
        hand.add(deck.draw());
    }

    public void playCharacterCard(int index, CharacterState state, int turn) throws InvalidOperationException {
        Card card = hand.get(index);
        if (index < hand.size() && card.getType().equals(CardType.CHARACTER)){ // kalo ga ini, maka throw error kalo wrong type
            hand.remove(index);  // keluarin dari tangan
            field.addCharacterCard(new CharacterCardInField((CharacterCard) card,state, turn));  // masukin ke field
        } // else : throw error
    }

    public void playCharacterCardByID(int id, CharacterState state, int turn) throws InvalidOperationException {
        Card card = hand.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(new NullCard());
        if (card.getType().equals(CardType.CHARACTER)) {
            hand.remove(card);
            field.addCharacterCard(new CharacterCardInField((CharacterCard) card, state, turn));
        } else {
            new InvalidOperationException("Play Character Card", "Invalid Card");
        }
    }

    public void playSkillAuraCard(int indexHand, int indexField) throws InvalidOperationException {
        if (indexHand < hand.size() && hand.get(indexHand).getType().equals(CardType.SKILL_AURA)){
            Card card = hand.remove(indexHand);
            field.connectCards(field.getCharacterCardByIdx(indexField), (SkillCard) card);
        } // else : throw error
    }

    public void playSkillCardByID (int id, int charID) throws InvalidOperationException {
        Card skillCard = hand.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(new NullCard());

        CharacterCardInField playCard = field.getCharCardList().stream()
                .filter(c -> c.getCard().getId() == charID)
                .findFirst()
                .orElse(null);

        if (skillCard.getType().equals(CardType.SKILL_AURA) && playCard!= null && getField().getSkillCardList().size() < getField().getFieldSize()) {
            hand.remove(skillCard);
            field.connectCards(playCard, (SkillCard) skillCard);
        }
    }

    // play skill destroy --> buat destroy ya, keknya dia mesti manggil game manager puny

    // play skill power up
    public void playSkillPowerUpCard(int indexHand, int indexField) throws InvalidOperationException {
        if (indexHand < hand.size() && hand.get(indexHand).getType().equals(CardType.SKILL_POWER_UP)){
            SkillCard card = (SkillCard) hand.remove(indexHand);
            field.connectCards(field.getCharacterCardByIdx(indexField), card);
        } // else : throw error
    }

    public void playLandCard(int indexHand) {

        if (hand.get(indexHand).getType().equals(CardType.LAND)) {
            LandCard card = (LandCard) hand.remove(indexHand);
            power.add(card.getElement(), 1);
        } // else throw error
    }

    public void playLandCardByID (int id) throws InvalidOperationException {
        Card card = hand.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);

        if (card == null) {
            throw new InvalidOperationException("Play Land Card", "Invalid Cards");
        }

        if (card.getType().equals(CardType.LAND)){
            hand.remove(card);
            power.add(card.getElement(), 1);
        }  else {
            throw new InvalidOperationException("Play Land Card", "Not A Land Card");
        }
    }

    // ini nanti dibuang aja
    public void removeCardFromHand(int index) {
        if (index < hand.size()){
            hand.remove(index);
        } // else : throw error
    }

    public void removeCharacterFromFieldByID(int id) throws InvalidOperationException {
        CharacterCardInField card = field.getCharacterCardByID(id);
        if (card == null) {
            throw new InvalidOperationException("Removing Summoned Character", "Card not found");
        }

        field.removeCharacterCard(card);
    }

    // buang
    public void removeCharacterFromField(int index) throws InvalidOperationException {
        if (index < field.getCharCardList().size()){
            field.removeCharacterCard(field.getCharacterCardByIdx(index));
        } // else : throw error
    }

    public void removeSkillCardByID (int id) throws InvalidOperationException {
        SkillCard card = field.getSkillCardByID(id);
        if (card == null) {
            throw new InvalidOperationException("Removing Summoned Character", "Card not found");
        }

        field.removeSkillCard(card);
    }

    // buang
    public void removeSkillCardFromField(int index) throws InvalidOperationException {
        if (index < field.getSkillCardList().size()){
            field.removeSkillCard(field.getSkillCardByIdx(index));
        } // else : throw error
    }

    public Deck getDeck() {
        return deck;
    }

    public Field getField() {
        return field;
    }

    public Hand getHand() {
        return hand;
    }

    public PlayerType getType() {
        return type;
    }

    public PowerManager getPower() {
        return power;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public boolean checkLose() {
        return healthPoint <= 0;
    }

    public void refreshState() {
        this.power.refresh();
        this.getField().getCharCardList()
                .forEach(c -> c.refresh());
    }
}