package com.mardarcu.pixeldash;

/**
 * Created by Owner on 10/2/2015.
 */
public class MenuScreenState {

    final public static int MAIN_SCREEN = 0;
    final public static int LEVEL_SCREEN = 1;
    final public static int SETTINGS_SCREEN = 2;


    private int CURRENT_STATE;
    MenuScreen menuScreen;

    public MenuScreenState(MenuScreen menuScreen, int ScreenState){
        this.menuScreen = menuScreen;
        CURRENT_STATE = ScreenState;
    }

    public void setMainScreen(){
        if(CURRENT_STATE == LEVEL_SCREEN){
            menuScreen.levelRootTable.setVisible(false);
            menuScreen.rootTable.setVisible(true);
        }
        if(CURRENT_STATE == SETTINGS_SCREEN){
            menuScreen.game.saveManager.writeSaveFile(menuScreen.game.state, false);
            menuScreen.loadLevels();
            menuScreen.settingsRootTable.setVisible(false);
            menuScreen.rootTable.setVisible(true);
        }
    }

    public void setScreen(int id){
        CURRENT_STATE = id;
    }

}
