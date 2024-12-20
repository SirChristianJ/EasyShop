# EasyShop
# Table of Contents
- [Purpose of this Project](#purpose-of-this-project)
- [Tech Stack](#tech-stack)
- [Challenges](#challenges)
- [Favorite Code Snippet](#favorite-code-snippet)
- [Future Improvements](#future-improvements)

---

## **Purpose of this Project**
This application is a full-stack project where we were given a frontend and fragments of a backend for an e-commerce site, *Easy Shop*. The task included:

- **Fixing Bugs**: Debugging existing backend REST controllers and SQL Data Access Objects (DAOs) for the `Products` class.
- **Enhancements**: Extending REST controllers and SQL DAOs for `Category`, `Profile`, and `ShoppingCart` classes.
- **API Testing**: Creating and testing new and existing API endpoints to ensure they:
  - Respond correctly with a payload or an empty body.
  - Adhere to CRUD functionality and RESTful standards.

---

## **Tech Stack**

### **Frontend (JavaScript):**
- `axios`
- `bootstrap`
- `jquery`
- `mustache`

### **Backend (Java/SQL):**
Dependencies managed by Maven:
- `jdbc driver/MySQL connector`
- `Spring Boot Framework`
- `commons-dbcp2` for `BasicDataSource`

---

## **Challenges**
One significant challenge was ensuring that CRUD functionalities in the REST controllers and DAOs adhered to RESTful standards. Key RESTful principles implemented include:

- **Data Format**: Using JSON or XML to send/receive data, with appropriate `Content-Type` and `Accept` headers.
- **Endpoints**: Naming endpoints with nouns instead of verbs.
- **HTTP Methods**: Using methods like `GET`, `POST`, `PUT`, and `DELETE` for specific actions.
- **Resource Nesting**: Efficiently nesting resources in URLs.
- **Collections**: Naming collections with plural nouns (e.g., `/products`).
- **Error Handling**: Leveraging HTTP status codes for errors.

---

## **Favorite Code Snippet**
While I didn't aim for creative code, focusing instead on functionality and RESTful practices, I found the `Principal` interface particularly useful. This interface represents an entity, typically a login ID. A provided snippet demonstrated retrieving a user ID from the `Principal` object, which I adapted into a helper method:

```java
private int retrieveUserId(Principal principal) {
    if (principal == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
    }

    // Get the currently logged-in username
    String userName = principal.getName();

    // Find the user in the database by username
    User user = userDao.getByUserName(userName);
    return user.getId();
}
