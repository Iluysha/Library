# Library
Library web application on Spring

## Functionality:

The reader registers in the system and then can:
- search (by author / title);
- place an order for a Book from the Catalog.
An unregistered reader cannot order a book.
For the catalog the ability to sort books has to be implemented:
- by name;
- by author;
- by publication;
- by date of publication.
The librarian gives the reader a book on a subscription or to the reading room. The book is
issued to the reader for a certain period. If the book is not returned within the specified
period, the reader will be fined. The book may be present in the library in one or more
copies. The system keeps track of the available number of books. Each user has a personal
account that displays registration information
And also:
1) for reader:
- list of books on the subscription and date of possible return (if the date is overdue, the
amount of the fine is displayed);
2) for librarian:
- list of readers' orders;
- list of readers and their subscriptions.
The system administrator has the rights:
- adding / deleting a book, editing information about the book;
- create / delete librarian;
- blocking / unblocking the user.
