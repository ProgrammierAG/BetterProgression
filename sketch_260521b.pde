float x, y;
float xSpeed, ySpeed;

void setup() {
  randomSeed(1);
  x = random(400);
  y = random(400);
  xSpeed = 4;
  ySpeed = 4;
  size(400, 400);
  background(0);
  fill(255);
  for(int i = 0; i<80; i++) {
    ellipse((int)random(400), (int)random(400), 4, 4);
  }
}
void draw() {
  randomSeed(1);
  background(0);
  fill(255);
  for(int i = 0; i<80; i++){
    ellipse((int)random(400), (int)random(400), 4, 4);
  }
  fill(255, 255, 0);
  comet();
  if (x > 400 - 10 || x < 0 + 10) {
    xSpeed = -xSpeed;
    
  }
  if (y > 400- 10 || y < 0 + 10) {
    ySpeed = -ySpeed; 
    
  }
   
}
void comet() { 
   ellipse(x, y, 20, 20);
   x += xSpeed;
   y += ySpeed;
}
