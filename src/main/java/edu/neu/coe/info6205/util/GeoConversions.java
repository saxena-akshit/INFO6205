package edu.neu.coe.info6205.util;

import java.util.Locale;
import java.util.Objects;

/**
 * For an explanation of UTM, see <a href="https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system">Universal Transverse Mercator coordinate system</a>
 * Original code thanks to <a href="https://stackoverflow.com/users/2548538/user2548538">...</a>.
 * Mind you the code was about as bad as any code could possibly be. I have tried to clean it up.
 */
public class GeoConversions {

    public static final int HalfCircleDegrees = 180;
    public static final double DEG2RAD = Math.PI / HalfCircleDegrees;
    public static final double RADIUS = 6366197.724; // radius that is used (but always adjusted by K_0) in meters
    // adjusted radius: 6363651.2449104
    public static final double RADIUS_P = 6399593.625; // polar radius in meters
    public static final double RADIUS_E = 6378137; // equatorial radius in meters (not used)
    public static final int E_0 = 500000; // in meters
    public static final double K_0 = 0.9996;
    public static final int N_0 = 10000000; // in meters
    public static final double E_constant = 0.0820944379;
    public static final double N_constant = 0.006739496742;
    public static final double N_constant_2 = 0.005054622556;
    public static final double N_constant3 = 4.258201531e-05;
    public static final int DEG_ZONE = 6;
    public static final int OFFSET_DEG = 183;

    static class Degrees {
        final double decimal;

        Degrees(int degrees, int minutes, double seconds) {
            decimal = degrees * 1.0 + minutes / 60.0 + seconds / 3600.0;
        }
    }

    static class UTM {
        final int Easting;
        final int Northing;
        final int Zone;
        final char Letter;

        public UTM(int easting, int northing, int zone, char letter) {
            Easting = easting;
            Northing = northing;
            Zone = zone;
            Letter = letter;
        }

        public Position toPosition() {
            return getPosition(Zone, Letter, Easting, Northing);
        }
    }

    public static UTM position2UTM(Position p) {
        int zone = (int) Math.floor(p.longitude / DEG_ZONE + 31);
        double adjustedLon = adjustLongitude(p.longitude, zone);
        double radiansLat = p.latitude * DEG2RAD;
        double sineLong = Math.sin(adjustedLon);
        double cosLong = Math.cos(adjustedLon);
        double cosLat = Math.cos(radiansLat);
        double product = cosLat * sineLong;
        double eConstantSqr = square(E_constant);
        double logValue = Math.log((1 + product) / (1 - product));
        double easting = 0.5 * logValue * K_0 * RADIUS_P / Math.pow((1 + eConstantSqr * square(cosLat)), 0.5) * (1 + eConstantSqr / 2 * square(0.5 * Math.log((1 + cosLat * Math.sin(adjustLongitude(p.longitude, zone))) / (1 - product))) * square(cosLat) / 3) + E_0;
        double var4 = radiansLat + sineOfHalfDegrees(p.latitude) / 2;
        double northing = (Math.atan(Math.tan(radiansLat) / cosLong) - radiansLat) * K_0 * RADIUS_P / Math.sqrt(1 + N_constant * square(cosLat)) * (1 + N_constant / 2 * square(0.5 * logValue) * square(cosLat)) + K_0 * RADIUS_P * (radiansLat - N_constant_2 * var4 + N_constant3 * (3 * var4 + sineOfHalfDegrees(p.latitude) * square(cosLat)) / 4 - 1.674057895e-07 * (5 * (3 * var4 + sineOfHalfDegrees(p.latitude) * square(cosLat)) / 4 + sineOfHalfDegrees(p.latitude) * square(cosLat) * square(cosLat)) / 3);
        char letter = 'N';
        if (getLetter(p.latitude) < 'M') {
            northing = northing + N_0;
            letter = 'S';
        }
        return new UTM(round(easting), round(northing), zone, letter);
    }

    static class Position {
        final double latitude;
        final double longitude;

        public Position(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position)) return false;
            Position position = (Position) o;
            return equalCoordinates(position.latitude, latitude) && equalCoordinates(position.longitude, longitude);
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }

        public boolean equalCoordinates(double x, double y) {
            return Math.abs(x - y) < 1E-4;
        }
    }

    static Position UTM2Position(String utm) {
        String[] parts = utm.split(" ");
        int zone = Integer.parseInt(parts[0]);
        char letter = parts[1].toUpperCase(Locale.ENGLISH).charAt(0);
        double easting = Double.parseDouble(parts[2]);
        double northing = Double.parseDouble(parts[3]);
        return getPosition(zone, letter, easting, northing);
    }

    static Position getPosition(int zone, char letter, double easting, double northing) {
        double hemisphere = letter > 'M' ? 'N' : 'S';
        double north = hemisphere == 'S' ? northing - N_0 : northing;
        double adjustedRadius = RADIUS * K_0;
        double northAdjusted = north / adjustedRadius;
        double v1 = square(Math.cos(northAdjusted));
        double v2 = K_0 * RADIUS_P / Math.sqrt(1 + N_constant * v1);
        double v3 = N_constant * square((easting - E_0) / v2) / 2;
        double v4 = v3 * v1 / 3;
        double v5 = square(N_constant * 3 / 4) * 5 / 3;
        double v6 = Math.sin(2 * northAdjusted);
        double v7 = northAdjusted + v6 / 2;
        double v8 = 3 * v7 + v6 * v1;
        double v9 = Math.pow(N_constant * 3 / 4, 3) * 35 / 27;
        double v10 = northAdjusted - N_constant * 3 / 4 * v7 + v5 * v8 / 4 - v9 * (5 * v8 / 4 + v6 * v1 * v1) / 3;
        double v11 = (north - K_0 * RADIUS_P * v10) / v2 * (1 - v3 * v1);
        double v12 = Math.atan(sinh((easting - E_0) / v2 * (1 - v4)) / Math.cos(v11 + northAdjusted));
        double v13 = Math.cos(v12) * Math.tan(v11 + northAdjusted);
        double v14 = Math.atan(v13) - northAdjusted;
        double v15 = (north / RADIUS / K_0 + (1 + N_constant * v1 - N_constant * Math.sin(northAdjusted) * Math.cos(northAdjusted) * v14 * 3 / 2) * v14) * HalfCircleDegrees / Math.PI;
        return new Position(roundFactored(v15, N_0), roundFactored(v12 * HalfCircleDegrees / Math.PI + zone * DEG_ZONE - OFFSET_DEG, N_0));
    }

    private static char getLetter(double lat) {
        if (lat < -72)
            return 'C';
        else if (lat < -64)
            return 'D';
        else if (lat < -56)
            return 'E';
        else if (lat < -48)
            return 'F';
        else if (lat < -40)
            return 'G';
        else if (lat < -32)
            return 'H';
        else if (lat < -24)
            return 'J';
        else if (lat < -16)
            return 'K';
        else if (lat < -8)
            return 'L';
        else if (lat < 0)
            return 'M';
        else if (lat < 8)
            return 'N';
        else if (lat < 16)
            return 'P';
        else if (lat < 24)
            return 'Q';
        else if (lat < 32)
            return 'R';
        else if (lat < 40)
            return 'S';
        else if (lat < 48)
            return 'T';
        else if (lat < 56)
            return 'U';
        else if (lat < 64)
            return 'V';
        else if (lat < 72)
            return 'W';
        else
            return 'X';
    }

    private static int round(double x) {
        return (int) Math.round(x);
    }

    private static double roundFactored(double x, int factor) {
        double y = Math.round(x * factor);
        return y / factor;
    }

    private static double sineOfHalfDegrees(final double x) {
        return Math.sin(2 * x * DEG2RAD);
    }

    private static double square(final double x) {
        return Math.pow(x, 2);
    }

    private static double adjustLongitude(double lon, final int zone) {
        return (lon - (DEG_ZONE * zone - OFFSET_DEG)) * DEG2RAD;
    }

    private static double sinh(double x) {
        return (Math.exp(x) - Math.exp(-x)) / 2;
    }
}