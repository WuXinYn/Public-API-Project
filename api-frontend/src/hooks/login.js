const saveUserToken = (userId, token) => {
  const userTokens = JSON.parse(localStorage.getItem('userTokens')) || {};
  userTokens[userId] = token;
  localStorage.setItem('userTokens', JSON.stringify(userTokens));
};

const getUserToken = (userId) => {
  const userTokens = JSON.parse(localStorage.getItem('userTokens')) || {};
  return userTokens[userId];
};

