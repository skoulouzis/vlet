#include <windows.h>

/*
 * Hello, World for Win32
 * gcc winhello.c -o winhello.exe
 */

int main(int argc, char *argv[])
{
   MessageBox(NULL, "Hello, world!", "Hello, world!", MB_OK);
   return 0;
}
