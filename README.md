# Simple Messaging Application
Simple messaging application ini adalah sebuah messaging app yang diimplementasikan dengan menggunakan RabbitMQ. Aplikasi ini memiliki spesifikasi sebagai berikut.

### Spesifikasi
Server instant messaging memiliki fungsionalitas:
- Menerima registrasi user baru (login name, password).
- Menerima penambahan friend untuk sebuah user.
- Menerima pembuatan grup baru, dimana user yang membuat grup akan menjadi admin grup tersebut. Pembuatan grup dapat langsung menyertakan anggota grup.
- Menambahkan anggota grup baru ke dalam sebuah grup.
- Mengeluarkan user dari grup.

Aplikasi instant messaging client memiliki fungsionalitas:
- Registrasi user baru.
- Login ke sistem (login name, password), setelah login, aplikasi dapat menampilkan notifikasi jika ada pesan baru (baik yang dikirim langsung maupun ke grup).
- Mengirimkan pesan ke user lain dan ke grup.
- Menambahkan friend.
- Membuat grup baru.
- Keluar dari grup.

Aplikasi ini dibuat dengan menggunakan bahasa Java dan diimplementasikan sebagai CLI (command line interface).

### Instalasi dan building
- Buka project server dan client dengan menggunakan Netbeans.
- Lakukan 'clean and build' pada masing-masing project.
- Jalankan 'run' pada server.
- Jalankan 'run' pada client.

### Cara menjalankan program
Pada folder bin, jalankan server terlebih dahulu.
```sh
$ cd bin
$ java -jar MessagingAppServer.jar
```
Setelah itu, jalankan client.
```sh
$ java -jar MessagingAppClient.jar
```
