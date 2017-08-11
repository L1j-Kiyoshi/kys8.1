package l1j.server.server.utils;

import java.util.Random;

public class CalcStat {

    private static Random rnd = new Random(System.nanoTime());

    private CalcStat() {
    }

    /**
     * Str 数値
     **/
    public static int calcDmgup(int str) {
        switch (str) {
        case 10:
        case 11:
            return 3;
        case 12:
        case 13:
            return 4;
        case 14:
        case 15:
            return 5;
        case 16:
        case 17:
            return 6;
        case 18:
        case 19:
            return 7;
        case 20:
        case 21:
            return 8;
        case 22:
        case 23:
            return 9;
        case 24:
            return 10;
        case 25:
            return 11;
        case 26:
        case 27:
            return 12;
        case 28:
        case 29:
            return 13;
        case 30:
        case 31:
            return 14;
        case 32:
        case 33:
            return 15;
        case 34:
            return 16;
        case 35:
            return 17;
        case 36:
        case 37:
            return 18;
        case 38:
        case 39:
            return 19;
        case 40:
        case 41:
            return 20;
        case 42:
        case 43:
            return 21;
        case 44:
            return 22;
        case 45:
        case 46:
            return 25;
        case 47:
            return 26;
        case 48:
        case 49:
            return 27;
        case 50:
            return 28;
        case 51:
        case 52:
            return 29;
        case 53:
            return 30;
        case 54:
        case 55:
            return 31;
        case 56:
            return 32;
        case 57:
        case 58:
            return 33;
        case 59:
            return 34;
        case 60:
        case 61:
            return 35;
        case 62:
            return 36;
        case 63:
        case 64:
            return 37;
        default:
            if (str >= 45)
                return 38;
            else
                return 2;
        }
    }

    public static int calcHitup(int str) {
        switch (str) {
        case 9:
        case 10:
            return 6;
        case 11:
            return 7;
        case 12:
        case 13:
            return 8;
        case 14:
            return 9;
        case 15:
        case 16:
            return 10;
        case 17:
            return 11;
        case 18:
        case 19:
            return 12;
        case 20:
            return 13;
        case 21:
        case 22:
            return 14;
        case 23:
            return 15;
        case 24:
            return 16;
        case 25:
            return 17;
        case 26:
            return 18;
        case 27:
        case 28:
            return 19;
        case 29:
            return 20;
        case 30:
        case 31:
            return 21;
        case 32:
            return 22;
        case 33:
        case 34:
            return 23;
        case 35:
            return 25;
        case 36:
        case 37:
            return 26;
        case 38:
            return 27;
        case 39:
        case 40:
            return 28;
        case 41:
            return 29;
        case 42:
        case 43:
            return 30;
        case 44:
            return 31;
        case 45:
        case 46:
            return 35;
        case 47:
            return 36;
        case 48:
        case 49:
            return 37;
        case 50:
            return 38;
        case 51:
        case 52:
            return 39;
        case 53:
            return 40;
        case 54:
        case 55:
            return 41;
        case 56:
            return 42;
        case 57:
        case 58:
            return 43;
        case 59:
            return 44;
        case 60:
        case 61:
            return 45;
        case 62:
            return 46;
        case 63:
        case 64:
            return 47;
        default:
            if (str >= 65)
                return 48;
            else
                return 5;
        }
    }

    public static int calcDmgCritical(int str) {
        if (str >= 65)
            return 4;
        else if (str >= 55)
            return 3;
        else if (str >= 45)
            return 2;
        else if (str >= 40)
            return 1;
        else
            return 0;
    }

    /**
     * Dex 数値
     **/
    public static int calcBowDmgup(int dex) {
        switch (dex) {
        case 9:
        case 10:
        case 11:
            return 3;
        case 12:
        case 13:
        case 14:
            return 4;
        case 15:
        case 16:
        case 17:
            return 5;
        case 18:
        case 19:
        case 20:
            return 6;
        case 21:
        case 22:
        case 23:
            return 7;
        case 24:
            return 8;
        case 25:
        case 26:
            return 9;
        case 27:
        case 28:
        case 29:
            return 10;
        case 30:
        case 31:
        case 32:
            return 11;
        case 33:
        case 34:
            return 12;
        case 35:
            return 13;
        case 36:
        case 37:
        case 38:
            return 14;
        case 39:
        case 40:
        case 41:
            return 15;
        case 42:
        case 43:
        case 44:
            return 16;
        case 45:
        case 46:
        case 47:
            return 20;
        case 48:
        case 49:
        case 50:
            return 21;
        case 51:
        case 52:
        case 53:
            return 22;
        case 54:
        case 55:
        case 56:
            return 23;
        case 57:
        case 58:
        case 59:
            return 24;
        case 60:
        case 61:
        case 62:
            return 25;
        default:
            if (dex >= 63)
                return 26;
            else
                return 2;
        }
    }

    public static int calcBowHitup(int dex) {
        switch (dex) {
        case 8:
            return -2;
        case 9:
            return -1;
        case 10:
            return 0;
        case 11:
            return 1;
        case 12:
            return 2;
        case 13:
            return 3;
        case 14:
            return 4;
        case 15:
            return 5;
        case 16:
            return 6;
        case 17:
            return 7;
        case 18:
            return 8;
        case 19:
            return 9;
        case 20:
            return 10;
        case 21:
            return 11;
        case 22:
            return 12;
        case 23:
            return 13;
        case 24:
            return 14;
        case 25:
            return 16;
        case 26:
            return 17;
        case 27:
            return 18;
        case 28:
            return 19;
        case 29:
            return 20;
        case 30:
            return 21;
        case 31:
            return 22;
        case 32:
            return 23;
        case 33:
            return 24;
        case 34:
            return 25;
        case 35:
            return 27;
        case 36:
            return 28;
        case 37:
            return 29;
        case 38:
            return 30;
        case 39:
            return 31;
        case 40:
            return 32;
        case 41:
            return 33;
        case 42:
            return 34;
        case 43:
            return 35;
        case 44:
            return 36;
        case 45:
            return 38;
        case 46:
            return 39;
        case 47:
            return 40;
        case 48:
            return 41;
        case 49:
            return 42;
        case 50:
            return 43;
        case 51:
            return 44;
        case 52:
            return 45;
        case 53:
            return 46;
        case 54:
            return 47;
        case 55:
            return 49;
        case 56:
            return 50;
        case 57:
            return 51;
        case 58:
            return 52;
        case 59:
            return 53;
        case 60:
            return 54;
        case 61:
            return 55;
        case 62:
            return 56;
        case 63:
            return 57;
        case 64:
            return 58;
        default:
            if (dex >= 65)
                return 60;
            else
                return -3;
        }
    }

    public static int calcBowCritical(int dex) {
        if (dex >= 65)
            return 4;
        else if (dex >= 55)
            return 3;
        else if (dex >= 45)
            return 2;
        else if (dex >= 40)
            return 1;
        else
            return 0;
    }

    public static int calcAc(int dex) {
        if (9 <= dex && dex <= 65)
            return -dex / 3;
        else if (dex > 65)
            return -31;
        else
            return -2;
    }

    public static int calcLongRangeAvoid(int dex) {
        try {
            if (dex < 0)
                return 3;
            if (dex > 45)
                return 22;
            return LongRange_Avoid[dex];
        } catch (Exception e) {
            e.printStackTrace();
            return 3;
        }
    }

    private static final int[] LongRange_Avoid = { 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 5, // 1~10
            5, 6, 6, 7, 7, 8, 8, 9, 9, 10, // 11~20
            10, 11, 11, 12, 12, 13, 13, 14, 14, 15, // 21~30
            15, 16, 16, 17, 17, 18, 18, 19, 19, 20, // 31~40
            20, 21, 21, 22, 22 };// 41~45

    /**
     * Int 数値
     **/
    public static int calcMagicDmg(int Int) {
        switch (Int) {
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
            return 1;
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
            return 2;
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
            return 4;
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
            return 5;
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
            return 7;
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
            return 8;
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
            return 12;
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
            return 13;
        case 55:
        case 56:
        case 57:
        case 58:
        case 59:
            return 15;
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
            return 16;
        default:
            if (Int >= 65)
                return 18;
            else
                return 0;
        }
    }

    public static int calcMagicHitUp(int Int) {
        switch (Int) {
        case 9:
        case 10:
        case 11:
            return -3;
        case 12:
        case 13:
        case 14:
            return -2;
        case 15:
        case 16:
        case 17:
            return -1;
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
            return 0;
        case 23:
        case 24:
            return 1;
        case 25:
            return 2;
        case 26:
        case 27:
        case 28:
            return 3;
        case 29:
        case 30:
        case 31:
            return 4;
        case 32:
        case 33:
        case 34:
            return 5;
        case 35:
        case 36:
        case 37:
            return 7;
        case 38:
        case 39:
        case 40:
            return 8;
        case 41:
        case 42:
        case 43:
            return 9;
        case 44:
        case 45:
        case 46:
            return 13;
        case 47:
        case 48:
        case 49:
            return 14;
        case 50:
        case 51:
        case 52:
            return 15;
        case 53:
        case 54:
        case 55:
            return 16;
        case 56:
        case 57:
        case 58:
            return 17;
        case 59:
        case 60:
        case 61:
            return 18;
        case 62:
        case 63:
        case 64:
            return 19;
        default:
            if (Int >= 65)
                return 20;
            else
                return -4;
        }
    }

    public static int calcMagicCritical(int Int) {
        if (Int >= 65)
            return 6;
        else if (Int >= 55)
            return 5;
        else if (Int >= 45)
            return 4;
        else if (Int >= 40)
            return 2;
        else if (Int >= 35)
            return 1;
        else
            return 0;
    }

    public static int calcMagicBonus(int chartype, int Int) {
        if (chartype != 3) {
            switch (Int) {
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 3;
            case 16:
            case 17:
                return 4;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return 5;
            case 24:
            case 25:
            case 26:
            case 27:
                return 6;
            case 28:
            case 29:
            case 30:
            case 31:
                return 7;
            case 32:
            case 33:
            case 34:
            case 35:
                return 8;
            case 36:
            case 37:
            case 38:
            case 39:
                return 9;
            case 40:
            case 41:
            case 42:
            case 43:
                return 10;
            case 44:
            case 45:
            case 46:
            case 47:
                return 11;
            case 48:
            case 49:
            case 50:
            case 51:
                return 12;
            case 52:
            case 53:
            case 54:
            case 55:
                return 13;
            case 56:
            case 57:
            case 58:
            case 59:
                return 14;
            case 60:
            case 61:
            case 62:
            case 63:
                return 15;
            default:
                if (Int >= 64)
                    return 16;
                else
                    return 2;
            }
        } else {
            if (16 <= Int && Int <= 65)
                return Int / 4 + 1;
            else if (Int > 65)
                return 16;
            else
                return 4;
        }
    }

    public static int calcDecreaseMp(int Int) {
        switch (Int) {
        case 9:
        case 10:
            return 6;
        case 11:
            return 7;
        case 12:
        case 13:
            return 8;
        case 14:
            return 9;
        case 15:
        case 16:
            return 10;
        case 17:
            return 11;
        case 18:
        case 19:
            return 12;
        case 20:
            return 13;
        case 21:
        case 22:
            return 14;
        case 23:
            return 15;
        case 24:
        case 25:
            return 16;
        case 26:
            return 17;
        case 27:
        case 28:
            return 18;
        case 29:
            return 19;
        case 30:
        case 31:
            return 20;
        case 32:
            return 21;
        case 33:
        case 34:
            return 22;
        case 35:
            return 23;
        case 36:
        case 37:
            return 24;
        case 38:
            return 25;
        case 39:
        case 40:
            return 26;
        case 41:
            return 27;
        case 42:
        case 43:
            return 28;
        case 44:
            return 29;
        case 45:
        case 46:
            return 30;
        case 47:
            return 31;
        case 48:
        case 49:
            return 32;
        case 50:
            return 33;
        case 51:
        case 52:
            return 34;
        case 53:
            return 35;
        case 54:
        case 55:
            return 36;
        case 56:
            return 37;
        case 57:
        case 58:
            return 38;
        case 59:
            return 39;
        case 60:
        case 61:
            return 40;
        case 62:
            return 41;
        case 63:
        case 64:
            return 42;
        default:
            if (Int >= 65)
                return 43;
            else
                return 5;
        }
    }

    public static int increaseMp(int type, byte Wis) {

        return rnd.nextInt(MaxincreaseMp(type, Wis)) + MinincreaseMp(type, Wis);
    }

    public static int MinincreaseMp(int type, int Wis) {
        int randommp = 0;
        switch (type) {
        case 0:
            if (Wis <= 14)
                randommp = 3;
            else if (Wis <= 19)
                randommp = 4;
            else if (Wis <= 24)
                randommp = 5;
            else if (Wis <= 29)
                randommp = 6;
            else if (Wis <= 34)
                randommp = 7;
            else if (Wis <= 39)
                randommp = 8;
            else if (Wis <= 44)
                randommp = 9;
            else
                randommp = 10;
            break;
        case 1:
        case 7:
            if (Wis <= 9)
                randommp = 0;
            else if (Wis <= 14)
                randommp = 1;
            else if (Wis <= 24)
                randommp = 2;
            else if (Wis <= 28)
                randommp = 3;
            else if (Wis <= 39)
                randommp = 4;
            else if (Wis <= 44)
                randommp = 5;
            else
                randommp = 6;
            break;
        case 2:
            if (Wis <= 14)
                randommp = 4;
            else if (Wis <= 19)
                randommp = 5;
            else if (Wis <= 24)
                randommp = 7;
            else if (Wis <= 29)
                randommp = 8;
            else if (Wis <= 34)
                randommp = 10;
            else if (Wis <= 39)
                randommp = 11;
            else if (Wis <= 44)
                randommp = 13;
            else
                randommp = 14;
            break;
        case 3:
            if (Wis <= 14)
                randommp = 6;
            else if (Wis <= 19)
                randommp = 8;
            else if (Wis <= 24)
                randommp = 10;
            else if (Wis <= 29)
                randommp = 12;
            else if (Wis <= 34)
                randommp = 14;
            else if (Wis <= 39)
                randommp = 16;
            else if (Wis <= 44)
                randommp = 18;
            else
                randommp = 20;
            break;
        case 4:
            if (Wis <= 14)
                randommp = 4;
            else if (Wis <= 19)
                randommp = 5;
            else if (Wis <= 24)
                randommp = 7;
            else if (Wis <= 29)
                randommp = 8;
            else if (Wis <= 34)
                randommp = 10;
            else if (Wis <= 39)
                randommp = 11;
            else if (Wis <= 44)
                randommp = 13;
            else
                randommp = 14;
            break;
        case 5:
            if (Wis <= 14)
                randommp = 2;
            else if (Wis <= 24)
                randommp = 3;
            else if (Wis <= 29)
                randommp = 4;
            else if (Wis <= 39)
                randommp = 5;
            else if (Wis <= 44)
                randommp = 6;
            else
                randommp = 7;
            break;
        case 6:
            if (Wis <= 14)
                randommp = 4;
            else if (Wis <= 19)
                randommp = 6;
            else if (Wis <= 24)
                randommp = 7;
            else if (Wis <= 29)
                randommp = 9;
            else if (Wis <= 34)
                randommp = 11;
            else if (Wis <= 39)
                randommp = 12;
            else if (Wis <= 44)
                randommp = 14;
            else
                randommp = 16;
            break;
        default:
            break;
        }
        return randommp;
    }

    public static int MaxincreaseMp(int type, int Wis) {
        int randommp = 0;
        switch (type) {
        case 0:
            if (Wis <= 11)
                randommp = 4;
            else if (Wis <= 14)
                randommp = 5;
            else if (Wis <= 17)
                randommp = 6;
            else if (Wis <= 20)
                randommp = 7;
            else if (Wis <= 23)
                randommp = 8;
            else if (Wis <= 26)
                randommp = 9;
            else if (Wis <= 29)
                randommp = 10;
            else if (Wis <= 32)
                randommp = 11;
            else if (Wis <= 35)
                randommp = 12;
            else if (Wis <= 38)
                randommp = 13;
            else if (Wis <= 41)
                randommp = 14;
            else if (Wis <= 44)
                randommp = 15;
            else
                randommp = 16;
            break;
        case 1:
        case 7:
            if (Wis <= 8)
                randommp = 1;
            else if (Wis <= 14)
                randommp = 2;
            else if (Wis <= 17)
                randommp = 3;
            else if (Wis <= 23)
                randommp = 4;
            else if (Wis <= 26)
                randommp = 5;
            else if (Wis <= 32)
                randommp = 6;
            else if (Wis <= 35)
                randommp = 7;
            else if (Wis <= 41)
                randommp = 8;
            else if (Wis <= 44)
                randommp = 9;
            else
                randommp = 10;
            break;
        case 2:
            if (Wis <= 14)
                randommp = 7;
            else if (Wis <= 17)
                randommp = 8;
            else if (Wis <= 20)
                randommp = 10;
            else if (Wis <= 23)
                randommp = 11;
            else if (Wis <= 26)
                randommp = 13;
            else if (Wis <= 29)
                randommp = 14;
            else if (Wis <= 32)
                randommp = 16;
            else if (Wis <= 35)
                randommp = 17;
            else if (Wis <= 38)
                randommp = 19;
            else if (Wis <= 41)
                randommp = 20;
            else if (Wis <= 44)
                randommp = 22;
            else
                randommp = 23;
            break;
        case 3:
            if (Wis <= 14)
                randommp = 10;
            else if (Wis <= 17)
                randommp = 12;
            else if (Wis <= 20)
                randommp = 14;
            else if (Wis <= 23)
                randommp = 16;
            else if (Wis <= 26)
                randommp = 18;
            else if (Wis <= 29)
                randommp = 20;
            else if (Wis <= 32)
                randommp = 22;
            else if (Wis <= 35)
                randommp = 24;
            else if (Wis <= 38)
                randommp = 26;
            else if (Wis <= 41)
                randommp = 28;
            else if (Wis <= 44)
                randommp = 30;
            else
                randommp = 32;
            break;
        case 4:
            if (Wis <= 11)
                randommp = 5;
            else if (Wis <= 14)
                randommp = 7;
            else if (Wis <= 17)
                randommp = 8;
            else if (Wis <= 20)
                randommp = 10;
            else if (Wis <= 26)
                randommp = 13;
            else if (Wis <= 29)
                randommp = 14;
            else if (Wis <= 32)
                randommp = 16;
            else if (Wis <= 35)
                randommp = 17;
            else if (Wis <= 38)
                randommp = 19;
            else if (Wis <= 41)
                randommp = 20;
            else
                randommp = 22;
            break;
        case 5:
            if (Wis <= 14)
                randommp = 3;
            else if (Wis <= 17)
                randommp = 4;
            else if (Wis <= 23)
                randommp = 5;
            else if (Wis <= 26)
                randommp = 6;
            else if (Wis <= 29)
                randommp = 7;
            else if (Wis <= 35)
                randommp = 8;
            else if (Wis <= 38)
                randommp = 9;
            else if (Wis <= 44)
                randommp = 10;
            else
                randommp = 11;
            break;
        case 6:
            if (Wis <= 14)
                randommp = 7;
            else if (Wis <= 17)
                randommp = 9;
            else if (Wis <= 20)
                randommp = 11;
            else if (Wis <= 23)
                randommp = 12;
            else if (Wis <= 26)
                randommp = 14;
            else if (Wis <= 29)
                randommp = 16;
            else if (Wis <= 32)
                randommp = 18;
            else if (Wis <= 35)
                randommp = 19;
            else if (Wis <= 38)
                randommp = 21;
            else if (Wis <= 41)
                randommp = 23;
            else if (Wis <= 44)
                randommp = 24;
            else
                randommp = 26;
            break;
        default:
            break;
        }
        return randommp;
    }

    public static int calcMpr(int wis) {
        if (wis <= 9)
            return 1;
        else if (wis <= 14)
            return 2;
        else if (wis <= 19)
            return 3;
        else if (wis <= 24)
            return 4;
        else if (wis <= 29)
            return 6;
        else if (wis <= 34)
            return 7;
        else if (wis <= 39)
            return 9;
        else if (wis <= 44)
            return 10;
        else if (wis <= 49)
            return 14;
        else if (wis <= 54)
            return 15;
        else if (wis <= 59)
            return 16;
        else if (wis <= 64)
            return 17;
        else
            return 19;
    }

    public static int calcMprPotion(int wis) {
        switch (wis) {
        case 12:
        case 13:
            return 2;
        case 14:
        case 15:
            return 3;
        case 16:
        case 17:
            return 4;
        case 18:
        case 19:
            return 6;
        case 20:
        case 21:
            return 7;
        case 22:
        case 23:
            return 8;
        case 24:
        case 25:
            return 9;
        case 26:
        case 27:
            return 10;
        case 28:
        case 29:
            return 11;
        case 30:
        case 31:
            return 12;
        case 32:
        case 33:
            return 13;
        case 34:
            return 14;
        case 35:
            return 15;
        case 36:
        case 37:
            return 16;
        case 38:
        case 39:
            return 17;
        case 40:
        case 41:
            return 18;
        case 42:
        case 43:
            return 19;
        case 44:
            return 20;
        case 45:
            return 23;
        case 46:
        case 47:
            return 24;
        case 48:
        case 49:
            return 25;
        case 50:
        case 51:
            return 26;
        case 52:
        case 53:
            return 27;
        case 54:
        case 55:
            return 28;
        case 56:
        case 57:
            return 29;
        case 58:
        case 59:
            return 30;
        case 60:
        case 61:
            return 31;
        case 62:
        case 63:
            return 32;
        default:
            if (wis >= 64)
                return 33;
            else
                return 1;
        }
    }

    public static int calcStatMr(int type, int wis) {
        int mr = 0;
        switch (type) {
        case 0:
            if (11 <= wis && wis <= 65)
                mr = 10 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 230;
            break;
        case 1:
        case 7:
            if (11 <= wis && wis <= 65)
                mr = (wis - 10) * 4;
            else if (wis > 65)
                mr = 220;
            break;
        case 2:
            if (12 <= wis && wis <= 65)
                mr = 25 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 245;
            break;
        case 3:
            if (14 <= wis && wis <= 65)
                mr = 15 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 235;
            break;
        case 4:
            if (10 <= wis && wis <= 65)
                mr = 10 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 230;
            break;
        case 5:
            if (10 <= wis && wis <= 65)
                mr = 18 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 238;
            break;
        case 6:
            if (14 <= wis && wis <= 65)
                mr = 20 + (wis - 10) * 4;
            else if (wis > 65)
                mr = 240;
            break;
        default:
            break;
        }
        return mr;
    }

    public static short increaseHp(int type, byte Con) {

        int randomhp = 0;
        if (Con <= 12)
            randomhp = 12 + rnd.nextInt(2);
        else if (Con <= 25)
            randomhp = Con + rnd.nextInt(2);
        else if (Con <= 26)
            randomhp = 25 + rnd.nextInt(2);
        else if (Con <= 28)
            randomhp = 26 + rnd.nextInt(2);
        else if (Con <= 30)
            randomhp = 27 + rnd.nextInt(2);
        else if (Con <= 32)
            randomhp = 28 + rnd.nextInt(2);
        else if (Con <= 34)
            randomhp = 29 + rnd.nextInt(2);
        else if (Con <= 36)
            randomhp = 30 + rnd.nextInt(2);
        else if (Con <= 38)
            randomhp = 31 + rnd.nextInt(2);
        else if (Con <= 40)
            randomhp = 32 + rnd.nextInt(2);
        else if (Con <= 42)
            randomhp = 33 + rnd.nextInt(2);
        else if (Con <= 44)
            randomhp = 34 + rnd.nextInt(2);
        else if (Con <= 46)
            randomhp = 35 + rnd.nextInt(2);
        else if (Con <= 48)
            randomhp = 36 + rnd.nextInt(2);
        else if (Con <= 50)
            randomhp = 37 + rnd.nextInt(2);
        else if (Con <= 52)
            randomhp = 38 + rnd.nextInt(2);
        else if (Con <= 54)
            randomhp = 39 + rnd.nextInt(2);
        else if (Con <= 56)
            randomhp = 40 + rnd.nextInt(2);
        else if (Con <= 58)
            randomhp = 41 + rnd.nextInt(2);
        else if (Con <= 60)
            randomhp = 42 + rnd.nextInt(2);
        else if (Con <= 62)
            randomhp = 43 + rnd.nextInt(2);
        else if (Con <= 64)
            randomhp = 44 + rnd.nextInt(2);
        else
            randomhp = 45 + rnd.nextInt(2);
        if (type == 1 || type == 7)
            randomhp += 5;
        else if (type == 2)
            randomhp -= 2;
        else if (type == 3)
            randomhp -= 5;
        else if (type == 4)
            randomhp -= 1;
        else if (type == 5)
            randomhp += 1;
        else if (type == 6)
            randomhp -= 3;
        return (short) randomhp;
    }

    public static int calcHpr(int con) {
        switch (con) {
        case 12:
        case 13:
            return 6;
        case 14:
        case 15:
            return 7;
        case 16:
        case 17:
            return 8;
        case 18:
        case 19:
            return 9;
        case 20:
        case 21:
            return 10;
        case 22:
        case 23:
            return 11;
        case 24:
            return 12;
        case 25:
            return 13;
        case 26:
        case 27:
            return 14;
        case 28:
        case 29:
            return 15;
        case 30:
        case 31:
            return 16;
        case 32:
        case 33:
            return 17;
        case 34:
            return 18;
        case 35:
            return 19;
        case 36:
        case 37:
            return 20;
        case 38:
        case 39:
            return 21;
        case 40:
        case 41:
            return 22;
        case 42:
        case 43:
            return 23;
        case 44:
            return 24;
        case 45:
            return 27;
        case 46:
        case 47:
            return 28;
        case 48:
        case 49:
            return 29;
        case 50:
        case 51:
            return 30;
        case 52:
        case 53:
            return 31;
        case 54:
            return 32;
        case 55:
            return 33;
        case 56:
        case 57:
            return 34;
        case 58:
        case 59:
            return 35;
        case 60:
        case 61:
            return 36;
        case 62:
        case 63:
            return 37;
        case 64:
            return 38;
        default:
            if (con >= 65)
                return 39;
            else
                return 5;
        }
    }

    public static int calcHprPotion(int con) {
        if (con >= 65)
            return 9;
        else if (con >= 60)
            return 8;
        else if (con >= 55)
            return 7;
        else if (con >= 50)
            return 6;
        else if (con >= 45)
            return 5;
        else if (con >= 40)
            return 4;
        else if (con >= 35)
            return 3;
        else if (con >= 30)
            return 2;
        else if (con >= 20)
            return 1;
        return 0;
    }

    public static short PureHp(int type, int Con) {
        int randomhp = 0;
        if (Con <= 12)
            randomhp = 12;
        else if (Con <= 25)
            randomhp = Con;
        else if (Con <= 26)
            randomhp = 25;
        else if (Con <= 28)
            randomhp = 26;
        else if (Con <= 30)
            randomhp = 27;
        else if (Con <= 32)
            randomhp = 28;
        else if (Con <= 34)
            randomhp = 29;
        else if (Con <= 36)
            randomhp = 30;
        else if (Con <= 38)
            randomhp = 31;
        else if (Con <= 40)
            randomhp = 32;
        else if (Con <= 42)
            randomhp = 33;
        else if (Con <= 44)
            randomhp = 34;
        else if (Con <= 46)
            randomhp = 35;
        else if (Con <= 48)
            randomhp = 36;
        else if (Con <= 50)
            randomhp = 37;
        else if (Con <= 52)
            randomhp = 38;
        else if (Con <= 54)
            randomhp = 39;
        else if (Con <= 56)
            randomhp = 40;
        else if (Con <= 58)
            randomhp = 41;
        else if (Con <= 60)
            randomhp = 42;
        else if (Con <= 62)
            randomhp = 43;
        else if (Con <= 64)
            randomhp = 44;
        else
            randomhp = 45;
        if (type == 1 || type == 7)
            randomhp += 5;
        else if (type == 2)
            randomhp -= 2;
        else if (type == 3)
            randomhp -= 5;
        else if (type == 4)
            randomhp -= 1;
        else if (type == 5)
            randomhp += 1;
        else if (type == 6)
            randomhp -= 3;
        return (short) randomhp;
    }

    public static int getMaxWeight(int str, int con) {
        int total = str + con;
        if (total > 150)
            total = 150;
        if (total % 2 != 0)
            total -= 1;
        return 1000 + (total * 100);
    }

    public static int getMagicBonus(int inte) {
        try {

            if (inte < 12)
                return 2;
            int temp = 2 + ((inte - 8) / 4);
            return temp/* 近距離_ダメージ[str] */;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
}
