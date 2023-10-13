import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import axios from "axios";


function RegisterForm(){
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [username, setUsername] = useState("");
    const[password, setPassword] = useState("")
    const [email, setEmail] = useState("")
    const [birthdate, setBirthdate] = useState("")

    async function save (event){
        event.preventDefault();
        try {
            const response = await axios.post("http://localhost:8181/auth/register",
                {
                    firstName: firstName,
                    lastName: lastName,
                    username: username,
                    email: email,
                    password: password,
                    birthdate: birthdate
                });
            if (response.status === 201 || response.status === 200) {
                alert("User " + firstName + " " + lastName + " registered.");
                setFirstName("");
                setLastName("");
                setEmail("");
                setPassword("");
                setUsername("");
                setBirthdate("");
            } else {
                alert("Something went wrong. Try again");
            }
        } catch (error) {
            // Output the error to the console for debugging
            console.error(error);
            alert("An error occurred. Please check the console for details.");
        }
    }


    return(
        <Form>
            Registering Form
            <br/>
            <br/>
            <Form.Group className="mb-3" controlId="firstName">
                <Form.Label>First name</Form.Label>
                <Form.Control
                    type="firstName"
                    placeholder="Enter first name"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="lastName">
                <Form.Label>Last name</Form.Label>
                <Form.Control
                    type="lastName"
                    placeholder="Last Name"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="username">
                <Form.Label>Username</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </Form.Group>


            <Form.Group className="mb-3" controlId="email">
                <Form.Label>Email</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="password">
                <Form.Label>Password</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </Form.Group>

            <Form.Group className="mb-3" controlId="birthdate">
                <Form.Label>Birthdate</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="Enter birthdate"
                    value={birthdate}
                    onChange={(e) => setBirthdate(e.target.value)}/>
            </Form.Group>
            <br/>
            <Button variant="primary"
                    type="submit"
                    onClick={save}
            >Submit
            </Button>
        </Form>

    )
}

export default RegisterForm;
