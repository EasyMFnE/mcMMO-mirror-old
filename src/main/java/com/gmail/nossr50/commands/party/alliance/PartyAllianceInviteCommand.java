package com.gmail.nossr50.commands.party.alliance;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyAllianceInviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                String targetName = Misc.getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName, true);

                if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return false;
                }

                Player target = mcMMOTarget.getPlayer();
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                String playerName = player.getName();

                if (player.equals(target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                    return true;
                }

                if (!mcMMOTarget.inParty()) {
                    player.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                if (PartyManager.inSameParty(player, target)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", targetName));
                    return true;
                }

                if (!mcMMOTarget.getParty().getLeader().equalsIgnoreCase(targetName)) {
                    player.sendMessage(LocaleLoader.getString("Party.Target.NotOwner", targetName));
                    return true;
                }

                Party playerParty = mcMMOPlayer.getParty();
                mcMMOTarget.setPartyAllianceInvite(playerParty);

                sender.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
                target.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.0", playerParty.getName(), playerName));
                target.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.1"));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "alliance", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
