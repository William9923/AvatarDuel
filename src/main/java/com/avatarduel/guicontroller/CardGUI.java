package com.avatarduel.guicontroller;

import com.avatarduel.model.card.Card;
import com.avatarduel.model.card.CharacterCard;
import com.avatarduel.model.card.LandCard;
import com.avatarduel.model.card.SkillAuraCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

public class CardGUI extends VBox {
    private Card data;
    private final String[] listBorderClass = {"water_border", "fire_border", "earth_border", "air_border"};
    @FXML private AnchorPane card_border;
    @FXML private Label card_name;
    @FXML private ImageView card_img;
    @FXML private Label card_desc;
    @FXML private HBox card_attributes;
    @FXML private Label card_atk;
    @FXML private Label card_def;
    @FXML private Label card_pow;

    public CardGUI() {
        System.out.println("CardGUI()");
    }

    public static CardGUI valueOf(Card card) {
        System.out.println("valueOf called");
        CardGUI result = new CardGUI();
        result.setCard(card);
        return result;
    }

    public void setData(CharacterCard card) {
        setCard(card);
        card_atk.setText("ATK : " + Integer.toString(card.getAttack()));
        card_def.setText("DEF : " + Integer.toString(card.getDefense()));
        card_pow.setText("POW : " + Integer.toString(card.getPower()));
    }
    public void setData(LandCard card) {
        setCard(card);
        card_atk.setText("");
        card_def.setText("");
        card_pow.setText("");
    }
    public void setData(SkillAuraCard card) {
        setCard(card);
        card_atk.setText("");
        card_def.setText("");
        card_pow.setText("POW : " + Integer.toString(card.getPower()));

    }
    public void setCard(Card card) {
        this.data = card;
        card_name.setText(this.data.getName());
        card_desc.setText(this.data.getDescription());
        card_img.imageProperty().set(getImage(card.getImage()));
        switch (card.getElement()) {
            case WATER:
                setBorderStyle("water_border");
                break;
            case FIRE:
                setBorderStyle("fire_border");
                break;
            case EARTH:
                setBorderStyle("earth_border");
                break;
            case AIR:
                setBorderStyle("air_border");
                break;
        }
    }

    private void setBorderStyle(String className) {
        card_border.getStyleClass().removeAll(listBorderClass);
        card_border.getStyleClass().add(className);
    }

    private Image getImage(String imgpath) {
        File f = new File("build/resources/main/com/avatarduel/card/" + imgpath.substring(8));
        return new Image("file:\\" + f.getAbsolutePath());
    }
}
