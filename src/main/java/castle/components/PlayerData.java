package castle.components;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Interval;
import castle.CastleRooms;
import castle.CastleRooms.UnitRoom;
import mindustry.entities.Units;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.Locale;

import static castle.CastleLogic.timer;

public class PlayerData {

    public static ObjectMap<String, PlayerData> datas = new ObjectMap<>();

    public Player player;
    public Interval interval;

    public int money = 0;
    public int income = 15;

    public boolean connected = true, hideHud = false;
    public Locale locale;

    public static Seq<PlayerData> datas() {
        return Seq.with(datas.values());
    }

    public PlayerData(Player player) {
        this.handlePlayerJoin(player);
    }

    public void update() {
        if (!connected || !player.team().active()) return;

        if (interval.get(60f)) {
            money += income;
            CastleRooms.rooms.each(room -> room.showLabel(this), room -> Call.label(player.con, room.label, 1f, room.getX(), room.getY()));
        }

        if (player.shooting) CastleRooms.rooms.each(room -> room.check(player.mouseX, player.mouseY) && room.canBuy(this), room -> room.buy(this));

        if (hideHud) return;
        StringBuilder hud = new StringBuilder(Bundle.format("ui.hud.balance", locale, money, income));

        if (Units.getCap(player.team()) <= player.team().data().unitCount) hud.append(Bundle.format("ui.hud.unit-limit", locale, Units.getCap(player.team())));

        hud.append(Bundle.format("ui.hud.timer", locale, timer));
        Call.setHudText(player.con, hud.toString());
    }

    public void handlePlayerJoin(Player player) {
        this.player = player;
        this.interval = new Interval();
        this.locale = Bundle.findLocale(player);

        this.connected = true;

        CastleRooms.rooms.each(room -> room instanceof UnitRoom, room -> Call.label(player.con, room.label, Float.MAX_VALUE, room.getX(), room.getY()));
    }
}
