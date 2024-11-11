# java-convenience-store-precourse

## Overview

- this project is convenience-store machine
   - ref : https://github.com/woowacourse-precourse/java-convenience-store-7
- below is Function list.
    - [ ] means not tested 
    - [x] means tested 

## Store Data

### Product
- [x] represent Product
- [x] apply promotion
- [x] check whether promotion is active or not based on date data
- [x] buy product with returning receipt and decreasing count
- [x] estimate result of promoted product when promotion is applied based on Date
- [x] return count of product which is not applied to promotion  
- [ ] check whether product is 0 or not

### Promotion
- [x] represent Promotion
- [x] check return count when promotion is applied
- [x] check whether date is able to apply promotion or not 
- [x] estimate result when promotion applied
- [x] check omitted return count 

### Receipt
- [ ] represent receipt
- [ ] combine receipt data when both product name is safe
- [ ] provide purchase info related to product , price and discount

### PromotionResult
- [ ] represent simulated result when promotion is applied
- [ ] provide simulated purchase info

### PurchaseRequest
- [ ] represent request for purchase
- [ ] access request info
- [ ] provide modifying request info 
   - [ ] decrease buy count
   - [ ] increase buy count

## Store Logic
- [ ] find product by name with options that find promoted or non-promoted or both
- [ ] access product data of repository by finding product
- [x] estimate result when promotion is applied to products
- [ ] calculate membership discount
- [x] validate name format

## Load logic
- [x] read src/main/resources/products.md file
- [x] read src/main/resources/promotions.md file
- [x] read file from specific path
- [x] validate file contents

## View logic
### Input view 
- [ ] print guide message before read input from user
- [ ] read purchase product list
- [ ] read answer whether promotion product omitted during buying is added or not
- [ ] read answer whether buy promotion product as regular price because of insufficient or not
- [ ] read answer whether apply membership discount or not
- [ ] read answer whether continue to purchase
- [ ] create purchase request based on purchase product list input
- [ ] retry read action when read invalid input
- [ ] print error message when read invalid input
- validate input data
  - [x] validate purchase product list
  - [x] validate purchase product
  - [x] extract elements from purchase product list
  - [x] validate answer Y/N format
  - 
### Output View
- [ ] print welcome message and selling product list
- [ ] print purchase product list
- [ ] print promotion return product list
- [ ] print bill 
## Promotion related

- [x] Promotion is able to applied whether today is on event days.
- (오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용한다.)

- [x] Promotion content is that returning 1 when consumer buys N products
- ( 프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.)

- [x] Product has only one promotion, multiple promotion is not applied one product
- 동일 상품에 여러 프로모션이 적용되지 않는다.

- [x] Promotion is applied to target Product
- N+1 프로모션이 각각 지정된 상품에 적용된다.


- [x] promotion should be applied to only promoted products
- 프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.
- [x] sell promoted product first if promotion is active. and sell non-promoted product if promoted product is insufficient count
- 프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.

## File Input/Output 
- [ ] Load Product List and Promotion List from File resource by using File IO.
- 구현에 필요한 상품 목록과 행사 목록을 파일 입출력을 통해 불러온다.
    - [x] create Product from string with resource File format
    - [x] crate Promotion from String with resource File format


## Utils

### Validation relation

- provide validation logic
    - [x] validate string whether string is numeric or not
    - [x] validate string whether numeric string is out of Int type range or not
    - [x] validate string whether numeric string is positive or not
    - [x] validate string whether string is blank or not
    - [x] validate number whether number is divisible by specific number or not
    - [x] validate number whether number is out of specific range or not
    - [x] validate list whether list is equal to specific size or not
    - [x] validate list whether list has duplicated element
    - [ ] validate String whether string is able to encoded to utf-8 or not
    - [ ] validate File whether File size is too large or not
    - [ ] validate File whether File is readable by java.io.file
    - [ ] validate File whether each line is validated based on format
    - [ ] validate File whether each column of line is validated based on format


### Throwing Exception logic

- throw Exception with printing exception message
    - [x] print Exception Message containing "[error]"
    - [x] throw IllegalArgumentException exception
    - [ ] throw IllegalStateException exception
    - [ ] deliver caused Exception

### Transforming logic

- provide type transform logic
   - [ ] transform Numeric String to Positive Integer
   - [ ] transform Integer List to concatenated String
   - [ ] transform string to LocalDate/LocalDateTime
   - [ ] transform List<String> to String by concatenating list