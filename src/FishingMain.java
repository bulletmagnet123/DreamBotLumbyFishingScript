import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(category = Category.FISHING, name = "Lumbridge Fisher", author = "Bulletmagnet", version = 0.3)
public class FishingMain extends AbstractScript implements ChatListener
{
    private Timer timeRan;
    private final Image paintBackground = getImage("https://i.imgur.com/bWrIucb.png");
    private DrawMouseUtil drawMouseUtil = new DrawMouseUtil();
    int shrimps = 0;
    int things_caught = 0;
    final Area fishArea = new Area(3244, 3152, 3238, 3143);


    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onStart() {
        timeRan = new Timer();
        while (Inventory.isFull()){
            bank();
        }
        if (fishArea.contains(getLocalPlayer()) && hasEquipment() == true && !Inventory.isFull()){
            fish();
        } else {

        }
    }

    @Override
    public int onLoop() {
        if (hasEquipment() == true && !Inventory.isFull()) {
            fish();
        } else {
            bank();
        }
        return 0;
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("You catch some shrimps") || message.getMessage().contains("You catch some anchovies") ) {
            things_caught++;
        }
    }

    public void fish() {
            if (fishArea.contains(getLocalPlayer()) && hasEquipment() == true && !Inventory.isFull()) {
                NPC fishingSpot = NPCs.closest("Fishing spot");
                if (fishingSpot != null) {
                    MethodProvider.log("Cast Net.");
                    sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 15000);
                    fishingSpot.interact("Net");
                    sleep(4000, 6000);
                    Mouse.moveMouseOutsideScreen();
                    sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 15000);
                }
            }
            while (!fishArea.contains(getLocalPlayer()) && Inventory.contains("Small fishing net")) {
                Walking.walk(fishArea);
                sleep(1000, 3000);
            }

        }

    public void bank() {
        if (hasEquipment() && !Inventory.isFull()){
            fish();
        } else if (Inventory.isEmpty()) {
            Walking.walk(org.dreambot.api.methods.container.impl.bank.Bank.getClosestBankLocation());
            Walking.walk(BankLocation.LUMBRIDGE.getCenter());
            sleep(1000, 2500);
            Bank.openClosest();
            sleep(500, 1200);
            Bank.withdraw("Small fishing net", 1);
            sleep(500, 1000);
            Bank.close();
            hasEquipment();
            fish();
        }
        if (Inventory.isFull()){
            Walking.walk(org.dreambot.api.methods.container.impl.bank.Bank.getClosestBankLocation());
            Walking.walk(BankLocation.LUMBRIDGE.getCenter());
            sleep(1000, 2500);
            Bank.openClosest();
            sleep(500, 1200);
            Bank.depositAllExcept("Small fishing net");
            Bank.close();
        }
    }

    public boolean hasEquipment() {
        boolean yes;
        if (Inventory.contains("Small fishing net")){
            yes = true;
        } else {
            yes = false;
        }
        return  yes;
    }

    public void DorB(boolean Drop, boolean Bank) {


    }
    @Override
    public void onPaint(Graphics2D g) {
        drawMouseUtil.drawRandomMouse(g);
        drawMouseUtil.setRainbow(true);
        g.drawString("Things cought: " + things_caught, 200, 450);
        g.drawString("Time Ran: " + timeRan.formatTime(), 200, 400);
        g.drawString("Current level:" + Skills.getRealLevel(Skill.FISHING), 200, 350);
    }


}
