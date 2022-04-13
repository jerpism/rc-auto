package com.example.joysticktest;

public interface JoystickListener {
    void onJoystickMoved(float xPercent, float yPercent, int source);
}
