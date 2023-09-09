package me.chris.eruption.util.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.StringJoiner;

@Data
@AllArgsConstructor
public class LocationUtil {

	private String world;

	private double x;
	private double y;
	private double z;

	private float yaw;
	private float pitch;

	public LocationUtil(double x, double y, double z) {
		this(x, y, z, 0.0F, 0.0F);
	}

	public LocationUtil(String world, double x, double y, double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}

	public LocationUtil(double x, double y, double z, float yaw, float pitch) {
		this("world", x, y, z, yaw, pitch);
	}

	public static LocationUtil fromBukkitLocation(Location location) {
		return new LocationUtil(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}

	public static LocationUtil stringToLocation(String string) {
		String[] split = string.split(", ");

		double x = Double.parseDouble(split[0]);
		double y = Double.parseDouble(split[1]);
		double z = Double.parseDouble(split[2]);

		LocationUtil locationUtil = new LocationUtil(x, y, z);

		if (split.length == 4) {
			locationUtil.setWorld(split[3]);
		} else if (split.length >= 5) {
			locationUtil.setYaw(Float.parseFloat(split[3]));
			locationUtil.setPitch(Float.parseFloat(split[4]));

			if (split.length >= 6) {
				locationUtil.setWorld(split[5]);
			}
		}
		return locationUtil;
	}

	public static String locationToString(LocationUtil loc) {
		StringJoiner joiner = new StringJoiner(", ");
		joiner.add(Double.toString(loc.getX()));
		joiner.add(Double.toString(loc.getY()));
		joiner.add(Double.toString(loc.getZ()));
		if (loc.getYaw() == 0.0f && loc.getPitch() == 0.0f) {
			if (loc.getWorld().equals("world")) {
				return joiner.toString();
			} else {
				joiner.add(loc.getWorld());
				return joiner.toString();
			}
		} else {
			joiner.add(Float.toString(loc.getYaw()));
			joiner.add(Float.toString(loc.getPitch()));
			if (loc.getWorld().equals("world")) {
				return joiner.toString();
			} else {
				joiner.add(loc.getWorld());
				return joiner.toString();
			}
		}
	}

	public Location toBukkitLocation() {
		return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public World toBukkitWorld() {
		if (this.world == null) {
			return Bukkit.getServer().getWorlds().get(0);
		} else {
			return Bukkit.getServer().getWorld(this.world);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LocationUtil location)) {
			return false;
		}

		return location.x == this.x && location.y == this.y && location.z == this.z
				&& location.pitch == this.pitch && location.yaw == this.yaw;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("x", this.x)
				.append("y", this.y)
				.append("z", this.z)
				.append("yaw", this.yaw)
				.append("pitch", this.pitch)
				.append("world", this.world)
				.toString();
	}
}
