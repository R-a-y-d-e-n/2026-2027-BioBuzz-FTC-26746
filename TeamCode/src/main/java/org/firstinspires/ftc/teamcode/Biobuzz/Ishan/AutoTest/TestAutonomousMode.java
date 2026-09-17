package org.firstinspires.ftc.teamcode.Biobuzz.Ishan.AutoTest;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name="TestAuto")

public class TestAutonomousMode extends OpMode {
    private final int ROBOT_WIDTH = 18;
    private Follower follower;
    private Timer pathtimer, actiontimer, opmodeTimer;
    private int pathstate;

    private Pose redStartingPose = new Pose(84,8,Math.toRadians(90)),
            pickup1pose = new Pose (20,35,Math.toRadians(180)),
            pickup2pose = new Pose(20,59.5,Math.toRadians(180)),
            pickup3pose = new Pose(20, 84.5,Math.toRadians(180)),
            scoringPosition = new Pose(35,125,Math.toRadians(145));
    private Path ScorePreload;
    private PathChain Grabpickup1,Scorepickup1 ;
    public void buildPaths() {
       ScorePreload = new Path(new BezierLine(redStartingPose,pickup1pose));
       ScorePreload.setLinearHeadingInterpolation(redStartingPose.getHeading(),pickup1pose.getHeading());

       Grabpickup1 = follower.pathBuilder()
               .addPath(new BezierLine(pickup1pose,scoringPosition ))
               .setLinearHeadingInterpolation(pickup1pose.getHeading(),scoringPosition.getHeading())
               .build();
    }
    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}


