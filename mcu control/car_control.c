#include "mbed.h"
#include <stdlib.h>
#include "Servo.h"

PwmOut led(LED1);
Serial bluetooth(D10, D3); // tx, rx
Serial pc(USBTX,USBRX);
Servo myservo(D4);
float forward;
float backward;
bool flag_sens = false;
AnalogIn ain(A1);

char chr;
bool flag = false;
int counter = 0;
char buffer_android[11];
char buffer_speed[5];
char buffer_angle[5];
float angle_value = 0.0;
float speed_value = 0.0;
float f;
void set_speed(float speed_value,float angle_value);
void get_speed_and_angle(char buffer_android[]);
void clear_buffer();
PwmOut releu(D7);

int main() {
    bluetooth.baud(9600);
    pc.baud(9600);
    releu = 0;
    while(1) { 
        if (bluetooth.readable())
        {      
            chr = bluetooth.getc();
            if ( chr == '#')
            {
                
            
                get_speed_and_angle(buffer_android);        
            }
            else
            {
                buffer_android[counter] = chr;
                counter++;
            }
        }
    }
}

void get_speed_and_angle(char buffer_get[])
{
   const char s = '$';
   char *token ="";
   
   token = strtok(buffer_get,& s);
   int i = 0;
  
   while( token != NULL ) {
      if ( i == 0 )
      {
        strcpy(buffer_angle,token);
        angle_value = atof(buffer_angle);      
      }
      if ( i == 1 )
      {
          strcpy(buffer_speed,token);
          speed_value = atof(buffer_speed);
      }
      i++;
      token = strtok(NULL, &s);
   }
    set_speed(speed_value,angle_value);     
}

void set_speed(float speed_value,float angle_value)
{
    pc.printf("%f\n", speed_value );
    
    
    myservo =  angle_value;
    releu = speed_value;
    clear_buffer();
}


void clear_buffer()
{
    memset(&buffer_android[0], 0, sizeof(buffer_android));
    memset(&buffer_angle[0], 0, sizeof(buffer_angle));
    memset(&buffer_speed[0], 0, sizeof(buffer_speed));
    counter = 0;
    angle_value = 0.0;
    speed_value = 0.0;
}
