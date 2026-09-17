package org.firstinspires.ftc.teamcode.Biobuzz.Nikolai.autonomous;

import static org.firstinspires.ftc.teamcode.Decode.MathUtils.mathFuncs.*;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Decode.Carousel;
import org.firstinspires.ftc.teamcode.Decode.Launcher;
import org.firstinspires.ftc.teamcode.Decode.MathUtils.vector;
import org.firstinspires.ftc.teamcode.Decode.mecanumConstants;



@Autonomous
public class Auto extends OpMode {

    private Timer pathTimer, opmodeTimer;
    public enum PathState {
        // Start Position_End Position
        // Drive > Movement State
        // Shoot > Attempt to score the Ball
        Drivestart,
        ShootPreloaded;
        }
    PathState pathState ;
    public void ripeForCollection(){
        carousel.incrementPosition(1 - mod(carousel.getPosition(), 1));
        inHalfPosition = false;
    };
    public void ripeForfiring(){
        carousel.incrementPosition(0.5 + mod(carousel.getPosition(), 1));
        inHalfPosition = true;
    };
    private Follower follower;
    public static Pose blueStartClose = new Pose(56,9, Math.toRadians(90));
    private final Pose shootPreload = new Pose(60,81, Math.toRadians(130));
    private final Pose redObelisk = new Pose(136, 136, 0);
    private final Pose blueObelisk = new Pose(8, 136, 0);
    private final Pose middle = new Pose(72,72);
    private Pose targetObelisk = redObelisk;

    private boolean fieldCentric = false;
    private vector targetVector = new vector();
    private Servo lift;
    private double targetSpeed = 20;
    private final double liftTimeToPosition = 0.8;
    private double startTime,
            prevTime = -0.0001;
    private boolean canMoveCarousel = false,
            canShoot,
            carouselMoving = false,
            inHalfPosition = false;
    private Carousel carousel = new Carousel();
    private Launcher launcher;
    private DcMotor carouselMotor,  leftIntake, rightIntake, leftFront, leftBack, rightFront, rightBack;
    private int debugVal = 0;
    private PathChain startShoot;

    public void buildPaths()
    {
        startShoot = follower.pathBuilder()
                .addPath(new BezierLine(blueStartClose, shootPreload))
                .setLinearHeadingInterpolation(blueStartClose.getHeading(),shootPreload.getHeading())
                .build();
    }

    public void setPathState(PathState newstate) {
        pathState = newstate;
        pathTimer.resetTimer();
    }

    public void statePathUpdate() {
        switch(pathState) {
            case Drivestart:
                follower.followPath(startShoot);
                setPathState(PathState.ShootPreloaded);
                break;
            case ShootPreloaded:
                //flywheel logic
                if (!follower.isBusy());
                telemetry.addLine("Path1 done");
                //pathState = PathState.nextpath
                break;
            default:
                telemetry.addLine("Not currently in a designated state");
                break;
        }
    }
    @Override
    public void init(){

        pathState = PathState.Drivestart;
        pathTimer = new Timer();
        opmodeTimer = new Timer();

        leftIntake = hardwareMap.get(DcMotor.class, "leftIntake");
        leftIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        rightIntake = hardwareMap.get(DcMotor.class, "rightIntake");
        rightIntake.setDirection(DcMotorSimple.Direction.FORWARD);

        launcher = new Launcher(hardwareMap, "launcher");
        launcher.speedLength = 10;
        launcher.PID.setConstants(1.75,0.3,2.0);
        launcher.PID.setIntegralLimit(0.4);

        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        follower = mecanumConstants.createFollower(hardwareMap);
        //follower.setStartingPose(blueStartClose == null ? new Pose() : blueStartClose);
        follower.setStartingPose(blueStartClose);
        follower.update();
        buildPaths();
    }

    @Override
    public void start(){
    opmodeTimer.resetTimer();
    setPathState(PathState.Drivestart);
    }

    @Override
    public void loop(){
        double deltaTime = time - prevTime;
        follower.update();
        statePathUpdate();
        launcher.update(deltaTime);



        double distance = Math.sqrt(
                Math.pow(targetObelisk.getX() - follower.getPose().getX(), 2) +
                        Math.pow(targetObelisk.getY() - follower.getPose().getY(), 2)
        );

        telemetry.addData("Pathstate", pathState);
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading", follower.getHeading());
        telemetry.addData("Time", pathTimer.getElapsedTimeSeconds());
    }
    @Override
    public void stop() {}
}
