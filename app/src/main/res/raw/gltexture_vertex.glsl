#version 320 es
layout (location = 0) vec3 vPos;

void main(){
   gl_Position = vec4(vPos, 1.0);
}