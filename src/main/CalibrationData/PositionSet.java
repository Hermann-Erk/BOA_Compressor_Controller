package main.CalibrationData;

/**
 * Created by Hermann on 14.09.2017.
 */
public class PositionSet {
    private int cornerCubeFrontPos;
    private int cornerCubeBackPos;
    private int roofMirrorFrontPos;
    private int roofMirrorBackPos;

    public PositionSet(int cornerCubeFrontPos, int cornerCubeBackPos, int roofMirrorFrontPos, int getRoofMirrorBackPos){
        this.cornerCubeFrontPos = cornerCubeFrontPos;
        this.cornerCubeBackPos = cornerCubeBackPos;
        this.roofMirrorFrontPos = roofMirrorFrontPos;
        this.roofMirrorBackPos = getRoofMirrorBackPos;
    }

    public int getCornerCubeFrontPos() {
        return cornerCubeFrontPos;
    }

    public int getCornerCubeBackPos() {
        return cornerCubeBackPos;
    }

    public int getRoofMirrorFrontPos() {
        return roofMirrorFrontPos;
    }

    public int getGetRoofMirrorBackPos() {
        return roofMirrorBackPos;
    }
}
