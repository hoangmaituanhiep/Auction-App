package app.functions;

abstract class User extends Entity {
    protected String userName;
    protected String password;

    abstract void register();

    abstract void signIn();
}

class Bidder extends User {

}
class Admin extends User {

}
class Seller extends User {

}
