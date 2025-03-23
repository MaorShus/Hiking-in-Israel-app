const connection = require("../models/user.model");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

const secretKey = "your_secret_key";

exports.register = (req, res) => {
  const { username, password } = req.body;

  // Hash password before storing
  bcrypt.hash(password, 10, (err, hash) => {
    if (err) {
      return res.status(500).send({ message: "Error hashing password" });
    }

    const query = "INSERT INTO users (username, password) VALUES (?, ?)";
    connection.query(query, [username, hash], (error, results) => {
      if (error) {
        return res.status(500).send({ message: "Error registering user" });
      }
      res.status(201).send({ message: "User registered successfully" });
    });
  });
};

exports.login = (req, res) => {
  const { username, password } = req.body;

  const query = "SELECT * FROM users WHERE username = ?";
  connection.query(query, [username], (error, results) => {
    if (error) {
      return res.status(500).send({ message: "Error fetching user" });
    }

    if (results.length === 0) {
      return res.status(404).send({ message: "User not found" });
    }

    const user = results[0];
    bcrypt.compare(password, user.password, (err, isMatch) => {
      if (err) {
        return res.status(500).send({ message: "Error comparing passwords" });
      }

      if (!isMatch) {
        return res.status(401).send({ message: "Invalid credentials" });
      }

      // Generate JWT token
      const token = jwt.sign(
        { id: user.id, username: user.username },
        secretKey,
        {
          expiresIn: "1h",
        }
      );

      res.status(200).send({ token });
    });
  });
};
