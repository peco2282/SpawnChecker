package net.awairo.minecraft.spawnchecker.mode;

public class CubeCoordinateIterator {
    private int startX;
    private int startY;
    private int startZ;
    private int xWidth;
    private int yHeight;
    private int zWidth;
    private int totalAmount;
    private int currentAmount;
    private int x;
    private int y;
    private int z;

    public CubeCoordinateIterator(int p_i50798_1_, int p_i50798_2_, int p_i50798_3_, int p_i50798_4_, int p_i50798_5_, int p_i50798_6_) {
        this.startX = p_i50798_1_;
        this.startY = p_i50798_2_;
        this.startZ = p_i50798_3_;
        this.xWidth = p_i50798_4_ - p_i50798_1_ + 1;
        this.yHeight = p_i50798_5_ - p_i50798_2_ + 1;
        this.zWidth = p_i50798_6_ - p_i50798_3_ + 1;
        this.totalAmount = this.xWidth * this.yHeight * this.zWidth;
    }

    public boolean hasNext() {
        if (this.currentAmount == this.totalAmount) {
            return false;
        } else {
            this.x = this.currentAmount % this.xWidth;
            int lvt_1_1_ = this.currentAmount / this.xWidth;
            this.y = lvt_1_1_ % this.yHeight;
            this.z = lvt_1_1_ / this.yHeight;
            ++this.currentAmount;
            return true;
        }
    }

    public int getX() {
        return this.startX + this.x;
    }

    public int getY() {
        return this.startY + this.y;
    }

    public int getZ() {
        return this.startZ + this.z;
    }

    public int numBoundariesTouched() {
        int lvt_1_1_ = 0;
        if (this.x == 0 || this.x == this.xWidth - 1) {
            ++lvt_1_1_;
        }

        if (this.y == 0 || this.y == this.yHeight - 1) {
            ++lvt_1_1_;
        }

        if (this.z == 0 || this.z == this.zWidth - 1) {
            ++lvt_1_1_;
        }

        return lvt_1_1_;
    }
}
