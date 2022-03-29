# Releases

## 0.1
  - DataReader interface created , with concrete implementation FileReader which reads from a file store in src/resources
  - Mapper interface which maps a string into list of objects, with implementation for JsonMapper using Jackson
  - OrderInitializer which initializes the list of objects

## 0.2
  - Added emulator which ingests orders into the kitchen at a particular ingestion rate
  - Added KitchenShelfManager which adds to or removes from different shelves based on the condition
  - Added ConsoleLogger which is a single point of logging for the application
  - Added Kitchen which processes multiple orders parallely and triggers a delivery courier
  - Added DeliveryCourier which waits for a random period of 2-7 seconds and
    then picks up the order from the shelf

## 0.3
- Split the multithreading helpers into threading package.
- Major refactoring of code to extract smaller classes
- Added several interfaces which hide a lot of implementation details, some refactoring still needs to be done. com.restaurantdeliverymanager.models package mostly contains the interfaces to be used by all other packages . This will be further refactored .
- Added Mockito to project and added CloudKitchenOrderTest, CourierServiceTest

## 1.0

 - Refactored all classes
 - Added unit tests for all classes
 - Cleaned up code
 - Renamed Bunch of Files






