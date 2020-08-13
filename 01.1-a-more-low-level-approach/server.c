#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <string.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>

void strip(char *s) {
    s[strcspn(s, "\r")] = '\0';
    s[strcspn(s, "\n")] = '\0';
}

int main(int argc, char *argv[]) {
    // final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
    int socketfd = socket(AF_INET, SOCK_STREAM, 0);
    int reuseopt = 1;
    
    if(socketfd < 0) {
        printf("Could not open socket!");
        return -1;
    }

    // final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
    setsockopt(socketfd, SOL_SOCKET, SO_REUSEADDR, &reuseopt, sizeof(int));

    struct sockaddr_in server_addr;

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(8080);

    // final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
    int bindret = bind(socketfd, (struct sockaddr *)&server_addr, sizeof(server_addr));
    
    if(bindret < 0) {
        printf("Could not bind to address!");
        return -1;
    }

    // final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
    listen(socketfd, 1);
    printf("Server started at port %d. PID: %d. Awaiting new client to connect...\r\n", 8080, -1);

    struct sockaddr_in client_address;
    int clientlen = sizeof(client_address);

    // final Socket client = serverSocker.accept();
    int clientfd = accept(socketfd, (struct sockaddr *)&client_address, &clientlen);
    printf("New client connected...File descriptor is %d\r\n", clientfd);

    char buffer[256];
    int should_stop = 0, read_ = 0;

    do {
        bzero(buffer, sizeof(buffer));

        // message = reader.readLine();
        read_ = read(clientfd, buffer, 256);
        
        if(read_ < 0) {
            printf("ERROR reading from client\r\n");
            continue;
        }

        strip(buffer);
        printf("Received message: %s. Sending echo...\r\n", buffer);

        if(strcmp(buffer, "quit") == 0) {
            should_stop = 1;
        }

        // out.println(message);
        write(clientfd, buffer, 256);
        printf("Message '%s' sent to client.\r\n", buffer);
    } while(should_stop == 0);
    
    printf("Client request quit. Shutting down...\r\n");
    
    // out.close();
    // reader.close();
    close(clientfd);

    // serverSocker.close();
    close(socketfd);

    return 0;
}
