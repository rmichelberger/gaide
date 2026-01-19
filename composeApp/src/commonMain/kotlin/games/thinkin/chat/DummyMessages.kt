package games.thinkin.chat

class DummyMessages {

    /*
    val messages = listOf(
        ChatMessage(
            messageType = MessageType.MODEL,
            text = "The\nImage\nShows\nA\nRetail"
        ),
    )*/


    val messages = listOf(
        ChatMessage(
            messageType = MessageType.MODEL,
            texts = "The image shows a retail shelf stocked mostly with Old Spice hygiene products. The shelf has four main levels filled with red packaging.\n\nOn the top shelf, starting at the top left corner, there is a white box with a picture of a person's legs. To the right of that are several red plastic containers for stick deodorants. The first two are extra-large sizes; one is Whitewater with a blue label and the other is Captain with a blue and shield-shaped label. Moving toward the center and right on this same shelf, there are regular-sized sticks: another Whitewater, a clear-cased Whitewater gel, Tigerclaw with a picture of a tiger, Original with a white diamond label, Tomorrowland with a red butterfly logo, and Nightpanther with a picture of a black panther.\n\nOn the second shelf down, starting from the left, there are two white Dove brand spray bottles. To the right of these is another long row of red stick deodorants. From left to right, the labels are Citron with a picture of a lime, Captain, Bearglove with a bear illustration, Wolfthorn with a wolf, Epic Legend with a golden crest, Restart with a green and white label, and Deep Sea with a blue wave illustration. On the far right of this shelf are black Garnier spray bottles.\n\nOn the third shelf down, which is at the center-level of the image, there is a white Garnier Mineral spray bottle on the far left. The rest of the shelf is tightly packed with tall, thin red spray cans. From left to right, these include Whitewater, Original, two cans of Captain, two cans of Nightpanther, Wolfthorn, Deep Sea, Restart, Tomorrowland, Bearglove, Epic Legend, and Oasis with a picture of white flowers.\n\nOn the fourth shelf down, near the bottom left corner, there is a large red gift set box featuring a man sitting in a chair. To the right of the box are several large, flat-sided red bottles of three-in-one body wash. These are arranged from left to right as Whitewater, two bottles of Captain, two bottles of Oasis with white floral graphics, Wolfthorn, and Deep Sea. In the bottom right corner, there are some translucent green bottles of a different brand.\n\nSmall electronic price tags are attached to the front of the shelves below each product.\n\nAre you looking for a specific type of product, such as a solid stick, a spray, or a body wash? Do you have a favorite scent you are trying to find, like Captain, Wolfthorn, or Oasis? Would you like me to read the price for any specific item?".lines()
                .filter { line -> line.isNotEmpty() }
        ),
        ChatMessage(
            messageType = MessageType.USER,
            texts = "how much is the Captain shower gel?".lines()
        ),
        ChatMessage(
            messageType = MessageType.MODEL,
            texts = "On the bottom shelf, where the large bottles of shower gel and body wash are located, there are two bottles of Captain 3-in-1 shower gel. These are the second and third large bottles from the left. Directly below the first Captain bottle, there is a bright red price tag that indicates the price is 2499 Forint for a 675 milliliter bottle. \n\nThere is another price tag further to the right for a 400 milliliter size of shower gel that costs 1299 Forint, though the bottles on the shelf appear to be the larger size.\n\nDo you want me to check the price for any other Captain products, such as the spray or the deodorant stick? Would you like to know the price for a different scent of shower gel, like Oasis or Wolfthorn? Are you interested in the price of the large gift set located on the bottom left?".lines()
                .filter { line -> line.isNotEmpty() }
        ),
        ChatMessage(messageType = MessageType.USER, texts = "oasis".lines()),
        ChatMessage(
            messageType = MessageType.MODEL,
            texts = "The Oasis scent is available in two different formats on these shelves.\n\nFirst, on the third shelf from the top, there is a red spray can of Oasis. You can find it on the far right side of the section dedicated to Old Spice spray cans. It is decorated with a picture of white flowers. The price tag directly below this spray can shows a price of 1549 Forint.\n\nSecond, on the bottom shelf, there are two large bottles of Oasis 3-in-1 shower gel. These are located in the center of the shelf, to the right of the Captain shower gel bottles. These bottles also feature the white flower graphic on their labels. There are two different price tags in front of these bottles. The one on the left indicates a price of 1299 Forint, while the one on the right shows 1649 Forint. This difference might be due to a specific sale or a misplaced tag for a different product size.\n\nWould you like me to check if there is an Oasis deodorant stick on the top two shelves? Do you want to know the price of the Oasis spray compared to other scents like Bearglove or Wolfthorn? Would you like me to look for any other specific scent in the shower gel section?".lines()
                .filter { line -> line.isNotEmpty() }
        ),
        ChatMessage(messageType = MessageType.USER, texts = "where is it?".lines()),
        ChatMessage(
            messageType = MessageType.MODEL,
            texts = "There are two locations for Oasis products on the shelves. For the Oasis spray can, look on the third shelf down from the top. It is located toward the right end of the long row of red spray cans. It is the very last red can in that specific Old Spice section before the shelf transitions to a blue bottle of a different brand. The label on this spray can features a picture of white flowers. For the Oasis shower gel, look on the very bottom shelf. These are large, flat red bottles located in the center of the shelf. There are two of them sitting side by side, positioned to the right of the two Captain shower gel bottles. These bottles also have the white flower illustration on the front label. \n\nDo you need help reaching the spray on the third shelf, or are you interested in the larger shower gel bottles on the bottom shelf? Would you like me to describe the labels of any other scents nearby to help you orient your hands? Do you want to know the exact price for either of these specific items again?".lines()
                .filter { line -> line.isNotEmpty() }
        ),
    )
}