const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendPushNotification = functions.database
    .ref("/chats/{chatId}/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
      const message = snapshot.val();
      const recipientId = message.receiverId;

      const userSnapshot = await admin.database()
          .ref(`users/${recipientId}`).once("value");
      const user = userSnapshot.val();

      if (!user || !user.fcmToken) {
        console.log("No token for user, cannot send notification");
        return null;
      }

      const payload = {
        notification: {
          title: "New Message",
          body: message.text,
          icon: "default_icon",
        },
        data: {
          chatId: context.params.chatId,
        },
      };

      return admin.messaging().sendToDevice(user.fcmToken, payload)
          .then((response) => {
            console.log("Successfully sent message:", response);
            return null;
          })
          .catch((error) => {
            console.log("Error sending message:", error);
          });
    });
