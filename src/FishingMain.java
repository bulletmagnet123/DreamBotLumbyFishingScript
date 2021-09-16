import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
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
import java.util.concurrent.TimeUnit;

@ScriptManifest(category = Category.FISHING, name = "Lumbridge Fisher", author = "Bulletmagnet", version = 1.1)
public class FishingMain extends AbstractScript implements ChatListener {

    public boolean BankOrDrop = true;
    private Timer timeRan;
    private final Image paintBackground = getImage("https://i.imgur.com/bWrIucb.png");
    private DrawMouseUtil drawMouseUtil = new DrawMouseUtil();
    int things_caught = 0;
    final Area fishArea = new Area(3244, 3152, 3238, 3143);
    int beginningXP;
    int currentXp;
    int xpGained;
    private final Color color1 = new Color(51, 51, 51, 147);
    private final Color color2 = new Color(138, 54, 15);
    private final Color color3 = new Color(255, 255, 255);
    private final BasicStroke stroke1 = new BasicStroke(5);


    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onStart() {

        SkillTracker.start(Skill.FISHING);
        beginningXP = Skills.getExperience(Skill.FISHING);
        Timer TTLV = new Timer();
        timeRan = new Timer();
        while (Inventory.isFull()) {
            bank();
        }
        if (fishArea.contains(getLocalPlayer()) && hasEquipment() == true && !Inventory.isFull()) {
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
        log("Bank or Drop boolean from GUI class = " + BankOrDrop);
        return 0;
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("You catch some shrimps") || message.getMessage().contains("You catch some anchovies")) {
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
        if (BankOrDrop) {
            if (hasEquipment() && !Inventory.isFull()) {
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
            if (Inventory.isFull()) {
                Walking.walk(org.dreambot.api.methods.container.impl.bank.Bank.getClosestBankLocation());
                Walking.walk(BankLocation.LUMBRIDGE.getCenter());
                sleep(1000, 2500);
                Bank.openClosest();
                sleep(500, 1200);
                Bank.depositAllExcept("Small fishing net");
                Bank.close();
            }
        } else {
            log("running drop method in main bank method");
            drop();
        }
    }

    public boolean hasEquipment() {
        boolean yes;
        if (Inventory.contains("Small fishing net")) {
            yes = true;
        } else {
            yes = false;
        }
        return yes;
    }

    public void drop() {
        log("running drop method");
        if (Inventory.isFull() && !BankOrDrop) {
            Inventory.dropAllExcept("Small fishing net");
        } else if (Inventory.isFull() && BankOrDrop) {
            bank();
        } else {
            fish();
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        drawMouseUtil.drawRandomMouse(g);
        drawMouseUtil.drawRandomMouseTrail(g);
        drawMouseUtil.setRandomColor();
        long ttl = SkillTracker.getTimeToLevel(Skill.FISHING);
        long timeTNL = ttl;
        g.setColor(Color.CYAN);
        g.drawRect(10, 250, 300, 400);


        Timer.formatTime(ttl);
        Polygon tile = Map.getPolygon(getLocalPlayer().getTile());

        g.drawPolygon(tile);
        currentXp = Skills.getExperience(Skill.FISHING);
        xpGained = currentXp - beginningXP;
        SkillTracker.getTimeToLevel(Skill.FISHING);
        g.setColor(color1);
        g.fillRect(10, 250, 300, 350);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.setColor(color3);
        g.drawString("Bullets Lumby Fisher", 140, 280);
        g.drawString("current xp: " + currentXp, 200, 450);
        g.drawString("Things cought: " + things_caught, 200, 425);
        g.drawString("Time Ran: " + timeRan.formatTime(), 200, 400);
        g.drawString("XP GAINED: " + xpGained, 200, 375);
        g.drawString("Current level: " + Skills.getRealLevel(Skill.FISHING), 200, 350);
        g.drawString("Time tell level: " + ft(timeTNL), 20,300);


    }
    private String ft(long duration)
    {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);
        } else {
            res = (days + ":" + hours + ":" + minutes + ":" + seconds);
        }
        return res;
    }
}