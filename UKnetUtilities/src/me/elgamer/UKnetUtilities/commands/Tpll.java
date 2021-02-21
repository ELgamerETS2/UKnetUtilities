package me.elgamer.UKnetUtilities.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.UKnetUtilities.projections.ModifiedAirocean;
import me.elgamer.UKnetUtilities.utils.LocationUtil;

public class Tpll implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot add a player to a region!");
			return true;
		}

		//Convert sender to player
		Player p = (Player) sender;

		if (!(p.hasPermission("vanillatpll.tpll"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		if(args.length==0) {
			return false;
		}

		String[] splitCoords = args[0].split(",");

		if(splitCoords.length==2&&args.length<3) { // lat and long in single arg
			args = splitCoords;
		}

		if(args[0].endsWith(",")) {
			args[0] = args[0].substring(0, args[0].length() - 1);
		}

		if(args.length>1&&args[1].endsWith(",")) {
			args[1] = args[1].substring(0, args[1].length() - 1);
		}

		if(args.length!=2) {
			return false;
		}

		double lon, lat;

		try {
			lat = Double.parseDouble(args[0]);
			lon = Double.parseDouble(args[1]);
		} catch(Exception e) {
			return false;
		}
		
		if (lat>90 || lat<-90) {
			p.sendMessage(ChatColor.RED + "Latitude is out of bounds, keep it between -90 and 90");
			return true;
		}
		
		if (lon>180 || lon<-180) {
			p.sendMessage(ChatColor.RED + "Longitude is out of bounds, keep it between -180 and 180");
			return true;
		}

		ModifiedAirocean projection = new ModifiedAirocean();

		double proj[] = projection.fromGeo(lon, lat);

		Location loc = null;
		
		final float pitch = p.getLocation().getPitch();
        final float yaw = p.getLocation().getYaw();

		try {
			loc = LocationUtil.getSafeDestination(new Location(p.getWorld(), proj[0], p.getWorld().getMaxHeight(), proj[1]));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loc = new Location(p.getWorld(), proj[0], loc.getY(), proj[1], yaw, pitch);
		
		if (loc.getY() == 0) {
			p.sendMessage(ChatColor.RED + "This location is above the void, you may not teleport here!");
			return true;
		}

		p.teleport(loc);
		p.sendMessage("Teleported " + p.getName() + " to " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());

		return true;
	}
	
	

}
