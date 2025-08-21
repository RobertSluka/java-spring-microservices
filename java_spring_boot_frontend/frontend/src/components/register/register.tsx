import React, { useState } from "react";
import api from "../../api/axiosConfig";
import { Container, Form, Button, Card, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { AxiosError } from "axios";
import backgroundPhoto from "../../assets/background_photo.jpg";


const Register: React.FC = () => {
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!email.trim() || !password.trim()) {
            setError("Email and password are required.");
            return;
        }

        try {
            const response = await api.post<{ token?: string }>("/register", {
                email: email.trim(),
                password: password.trim(),
            });

            console.log("Register Response:", response.data);

            const token = response.data.token || (response.data as unknown as string);

            if (token) {
                localStorage.setItem("token", token);
                setError(null);
                alert("Register successful!");
                navigate("/chat");
            } else {
                setError("Register failed! No token received.");
            }
        } catch (err: unknown) {
            const axiosErr = err as AxiosError<{ message?: string }>;
            const errorMsg =
                axiosErr.response?.data?.message || "Register failed!";
            setError(errorMsg);
        }
    };

    return (
        <Container
            fluid
            className="d-flex justify-content-center align-items-center vh-100"
            style={{
                height: "100vh",
                width: "100vw",
                padding: 0,
                backgroundImage: `url(${backgroundPhoto})`,
                backgroundSize: "cover",
                backgroundPosition: "center",
                backgroundRepeat: "no-repeat",
                position: "relative", }}
        >

            <Card
                style={{
                    width: "350px",
                    padding: "20px",
                    backgroundColor: "rgba(30,30,30,0.9)",
                    color: "#ffffff",
                    borderRadius: "10px",
                    boxShadow: "0px 4px 10px rgba(0,0,0,0.3)",
                    position: "relative", // keep above overlay
                    zIndex: 1,
                    backdropFilter: "blur(2px)",
                }}
            >
                <Card.Body>
                    <h2 className="text-center mb-4" style={{ color: "#ffffff" }}>
                        Register
                    </h2>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label style={{ color: "#bbbbbb" }}>Email</Form.Label>
                            <Form.Control
                                type="text"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="Enter your email"
                                required
                                style={{
                                    backgroundColor: "#2a2a2a",
                                    color: "#ffffff",
                                    borderColor: "#444",
                                }}
                            />
                        </Form.Group>

                        <Form.Group className="mb-4">
                            <Form.Label style={{ color: "#bbbbbb" }}>Password</Form.Label>
                            <Form.Control
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                                style={{
                                    backgroundColor: "#2a2a2a",
                                    color: "#ffffff",
                                    borderColor: "#444",
                                }}
                            />
                        </Form.Group>

                        <Button
                            variant="primary"
                            type="submit"
                            className="w-100"
                            style={{ backgroundColor: "#007bff", borderColor: "#0056b3" }}
                        >
                            Register
                        </Button>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Register;