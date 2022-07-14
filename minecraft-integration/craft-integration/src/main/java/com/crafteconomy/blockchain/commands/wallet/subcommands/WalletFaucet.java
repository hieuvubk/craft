package com.crafteconomy.blockchain.commands.wallet.subcommands;

import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.crafteconomy.blockchain.commands.SubCommand;
import com.crafteconomy.blockchain.core.request.BlockchainRequest;
import com.crafteconomy.blockchain.core.types.FaucetTypes;
import com.crafteconomy.blockchain.utils.Util;
import com.crafteconomy.blockchain.wallets.WalletManager;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

// TODO: Remove when live

public class WalletFaucet implements SubCommand {

    WalletManager walletManager = WalletManager.getInstance();
    int requiredWalletLength = CraftBlockchainPlugin.getInstance().getWalletLength();

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String wallet = null;
        long craftAmount = 0;

        if(args.length < 3) {
            Util.colorMsg(sender, "&cInvalid usage. &f&l/wallet faucet <wallet> <amount>");
            return;
        }
               
        wallet = args[1];

        // If they are requesting to give to a player
        if(!wallet.startsWith("craft")) {
            // not a wallet, check if it is a user. if so, get their wallet
            wallet = walletManager.getAddressFromName(args[1]);

            if(wallet == null) {
                Util.colorMsg(sender, "&cInvalid wallet / player:  " + args[1]);
                return;
            }
        }

        if(wallet == null || wallet.length() != requiredWalletLength) {
            Util.colorMsg(sender, "&cInvalid wallet address " + wallet + " ( length " + wallet.length() + " )");
            return;
        }

        try {
            craftAmount = Long.parseLong(args[2]);
            if(craftAmount <= 0) { return; }
        } catch (Exception e) {
            Util.colorMsg(sender, "&cInvalid amount " + args[2]);
            return;
        }
                
        // used only for outputs
        String reducedWallet = wallet.substring(0, 20) + "...";

        Util.colorMsg(sender, "&f&o[!] Faucet request sent for " + reducedWallet);   
        Util.colorMsg(sender, "&f&o[!] This may take up to ~45 seconds to process.");
                
        final String finalWallet = wallet;
        final long finalAmount = craftAmount;
        BlockchainRequest.depositCraftToAddress(wallet, craftAmount).thenAccept(faucet_status -> {
            // This runs in the Forked thread, not main (non blocking)
            // System.out.println("The future from WalletFaucet returned " + status);

            UUID userID = walletManager.getUUIDFromWallet(finalWallet);
            Player receiver = null;
            if(userID != UUID.fromString("00000000-0000-0000-0000-000000000000")) {
                receiver = Bukkit.getPlayer(userID);
            }       

            if(faucet_status == FaucetTypes.SUCCESS) {
                if(sender instanceof ConsoleCommandSender) {
                    Util.colorMsg(sender, "&fPayment Success! &fFauceted +" + finalAmount + "craft to their wallet: &a" + reducedWallet);                    
                }
                
                if(receiver != null && receiver.isOnline()) {
                    Util.colorMsg(receiver, "&aIncoming Payment! &fYou received +" + finalAmount + "ucraft to your wallet: &a" + reducedWallet);
                }                
            } else {
                Util.colorMsg(sender, "&c&o[!] ERROR: payment request failed for " + reducedWallet + ". Error: " + faucet_status);
            }
        });

     
    }
}
