import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserDoctor } from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink } from "react-router-dom";

const Header: React.FC = () => {
    const brandStyle: React.CSSProperties = { color: "white" };
    const navLinkClass = ({ isActive }: { isActive: boolean }) =>
        `nav-link${isActive ? " active" : ""}`;

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container fluid>
                <Navbar.Brand href="/login" style={brandStyle}>
                    <FontAwesomeIcon icon={faUserDoctor} /> Patient System
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="main-navbar" />
                <Navbar.Collapse id="main-navbar">
                    <Nav className="me-auto my-2 my-lg-0" navbarScroll style={{ maxHeight: "100px" }}>
                        <NavLink className={navLinkClass} to="/login">
                            Login
                        </NavLink>
                        <NavLink className={navLinkClass} to="/register">
                           Register
                        </NavLink>
                        <NavLink className={navLinkClass} to="/">
                            Chat
                        </NavLink>
                        <NavLink className={navLinkClass} to="/patients">
                            Doctor
                        </NavLink>
                    </Nav>

                    <Nav className="d-flex">
                        <NavLink to="/login" className="me-2">
                            <Button variant="outline-info">Login</Button>
                        </NavLink>
                        <NavLink to="/register">
                            <Button variant="outline-info">Register</Button>
                        </NavLink>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;