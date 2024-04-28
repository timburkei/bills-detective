export class UserProfile {
  id: string;
  name: string;
  username: string;
  email: string;
  image: string;

  constructor(  id: string, name: string, username: string, email: string, image: string) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.email = email;
    this.image = image;
  }
}
